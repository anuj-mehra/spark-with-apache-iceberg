// scalastyle:off
package com.spike.spark_iceberg.repository.hbase

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{CompareOperator, HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Result, ResultScanner, Scan, Table}
import org.apache.hadoop.hbase.filter.{BinaryComparator, FilterList, SingleColumnValueFilter}
import org.apache.hadoop.hbase.filter.FilterList.Operator
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{ArrayType, CalendarIntervalType, DataType, DataTypes, LongType, MapType, NullType, ObjectType, StringType, StructField, StructType}

import java.util.Base64
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class HBaseReadRepository(@transient conf: Configuration)(implicit sparkSession: SparkSession)
  extends Serializable with LazyLogging{

  private val columnFamily: String = "cf"

  def getFullTable(schema: StructType, cachingSize: Int): DataFrame = {
    val scan: Scan = new Scan()
    scan.setCaching(cachingSize)
    val proto: ClientProtos.Scan = ProtobufUtil.toScan(scan)
    conf.set(TableInputFormat.SCAN, Base64.getEncoder.encodeToString(proto.toByteArray))

    val hbaseRDD: RDD[(ImmutableBytesWritable, Result)] = sparkSession.sparkContext.newAPIHadoopRDD(
      conf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )

    val resultRDD: RDD[Result] = hbaseRDD.map(tuple => tuple._2)
    val rowRDD: RDD[Row] = resultRDD.map(p => {
      this.getRow(p, schema, columnFamily)
    })

    sparkSession.createDataFrame(rowRDD, schema)
  }

  def getFilteredData(schema: StructType, filterList: FilterList, cachingSize: Int): DataFrame = {

    val scan: Scan = new Scan()
    scan.setCaching(cachingSize)
    scan.setFilter(filterList)

    val proto: ClientProtos.Scan = ProtobufUtil.toScan(scan)
    conf.set(TableInputFormat.SCAN, Base64.getEncoder.encodeToString(proto.toByteArray))
    this.getFullTable(schema, cachingSize)
  }


  def getDataForGivenRowKeyPrefix(rowPrefix: String, schema: StructType, cachingSize: Int): DataFrame ={
    val scan: Scan = new Scan()
    scan.setCaching(cachingSize)
    scan.addFamily(Bytes.toBytes(columnFamily))

    val prefixFilter = Bytes.toBytes(rowPrefix)
    scan.setRowPrefixFilter(prefixFilter)

    val proto: ClientProtos.Scan = ProtobufUtil.toScan(scan)
    conf.set(TableInputFormat.SCAN, Base64.getEncoder.encodeToString(proto.toByteArray))
    val hbaseRDD: RDD[(ImmutableBytesWritable, Result)] = sparkSession.sparkContext.newAPIHadoopRDD(
      conf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )
    val resultRDD: RDD[Result] = hbaseRDD.map(tuple => tuple._2)
    val rowRDD: RDD[Row] = resultRDD.map(p => {
      this.getRow(p, schema, columnFamily)
    })

    sparkSession.createDataFrame(rowRDD, schema)
  }

  def getRowKeysUsingPrefixFilter(rowPrefix: String, schema: StructType, cachingSize: Int): DataFrame ={
    val scan: Scan = new Scan()
    scan.setCaching(cachingSize)
    scan.addFamily(Bytes.toBytes(columnFamily))

    val prefixFilter = Bytes.toBytes(rowPrefix)
    scan.setRowPrefixFilter(prefixFilter)

    val proto: ClientProtos.Scan = ProtobufUtil.toScan(scan)
    conf.set(TableInputFormat.SCAN, Base64.getEncoder.encodeToString(proto.toByteArray))
    val hbaseRDD: RDD[(ImmutableBytesWritable, Result)] = sparkSession.sparkContext.newAPIHadoopRDD(
      conf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )

    val rowKeyRDD: RDD[Row] = hbaseRDD.values.map(result => {
      val rowkey = Bytes.toString(result.getRow)
      new GenericRowWithSchema(Array(rowkey), schema)
    })

    sparkSession.createDataFrame(rowKeyRDD, schema)
  }

  def getDataOnRowKeys(keys: DataFrame, rowkeyName: String, tableName: String, schema: StructType): DataFrame ={

    val connectionConfig: Broadcast[HBaseConnectionConfig] = HBaseConnectionConfig(sparkSession, conf)
    keys.mapPartitions((rows: Iterator[Row]) => {
      val value: Table = this.getTable(connectionConfig, tableName)
      val keys = rows.map(row => row.getAs[String](rowkeyName))
      val results: Array[Result] = this.getBulkDataRecondsOnRowKeys(tableName, keys, value)
      val respRows: Array[Row] = results.map(result => this.getRow(result, schema, columnFamily))
      respRows.toIterator
    })(RowEncoder.apply(schema))
  }

  def getDataUsingPrefixFilterAndGivenColumn(prefixFilterAndColumnValueDf: DataFrame,
                                             responseSchema: StructType,
                                             singleColumnName: String,
                                             singleColumnDataType: DataType): DataFrame = {

    val connectionConfig: Broadcast[HBaseConnectionConfig] = HBaseConnectionConfig(sparkSession, conf)

    val respDf: DataFrame = prefixFilterAndColumnValueDf.mapPartitions(rowsInPartitions => {
      lazy val table = this.getTable(connectionConfig, connectionConfig.value.tableName)
      val hbaseRows = rowsInPartitions.flatMap(row => {
        val prefixKey = row.getAs[String]("prefix-keys")
        val scan: Scan = new Scan()
        val startRow = Bytes.toBytes(prefixKey)
        scan.setRowPrefixFilter(startRow)
        scan.addFamily(Bytes.toBytes(columnFamily))

        val filterList = new FilterList(Operator.MUST_PASS_ALL)
        val singleColumnValue = singleColumnDataType match {
          case DataTypes.LongType =>
            row.getAs[Long](singleColumnName)
          case DataTypes.StringType =>
            row.getAs[String](singleColumnName)
        }

        val columnValueFilter =
          this.createSingleColumnValueFilter(singleColumnName, singleColumnValue, CompareOperator.EQUAL)
        filterList.addFilter(columnValueFilter)
        scan.setFilter(columnValueFilter)

        val hbaseResults = new mutable.ListBuffer[Row]
        var scanner: ResultScanner = None.orNull
        try {
          scanner = table.getScanner(scan)
          var scanResult: Result = scanner.next
          while(scanResult != null){
            hbaseResults += this.getRow(scanResult, responseSchema, columnFamily)
            scanResult = scanner.next
          }
        } catch {
          case e:Exception => logger.error("Exception in hbase scan:", e)
        } finally {
          scanner == None.orNull match {
            case true =>
            case false =>
              scanner.close()
          }
        }
        hbaseResults
      })
      hbaseRows.toIterator
      })(RowEncoder.apply(responseSchema))

    respDf
  }

  def getDataForSetOfRowKeyPrefix(prefixDf: DataFrame, responseSchema: StructType, tableName: String): DataFrame = {

    val connectionConfig: Broadcast[HBaseConnectionConfig] = HBaseConnectionConfig(sparkSession, conf)
    val respDf: DataFrame = prefixDf.mapPartitions(rowsInPartitions => {
      lazy val table = this.getTable(connectionConfig, tableName)
      val hbaseRows: Iterator[Row] = rowsInPartitions.flatMap(row=> {
        val prefixKey = row.getAs[String]("prefix_keys")
        val scan: Scan = new Scan()
        val startRow = Bytes.toBytes(prefixKey)
        scan.setRowPrefixFilter(startRow)
        scan.addFamily(Bytes.toBytes(columnFamily))
        val filterList = new FilterList(Operator.MUST_PASS_ALL)
        scan.setFilter(filterList)

        val hbaseResults = new mutable.ListBuffer[Row]
        var scanner: ResultScanner = None.orNull
        try{
          scanner = table.getScanner(scan)
          var scanResult: Result = scanner.next
          while(scanResult  != null){
            hbaseResults += this.getRow(scanResult, responseSchema, columnFamily)
            scanResult = scanner.next
          }
        }catch{
          case e: Exception => logger.error("Exception in hbase scan:", e)
        }finally{
          scanner == None.orNull match {
            case true =>
            case false =>
              scanner.close()
          }
        }
        hbaseResults
      })
      hbaseRows.toIterator
    })(RowEncoder.apply(responseSchema))

    respDf
  }

  private def createSingleColumnValueFilter(key: String,
                                            value: Any,
                                            compareOp: CompareOperator = CompareOperator.EQUAL
                                           ): SingleColumnValueFilter ={
    val valueInBytes = value match {
      case _:String =>
        Bytes.toBytes(value.asInstanceOf[String])
      case _:Long =>
        Bytes.toBytes(value.asInstanceOf[Long])
    }
    new SingleColumnValueFilter(Bytes.toBytes(columnFamily), Bytes.toBytes(key), compareOp,
      new BinaryComparator(valueInBytes))
  }

  private def getBulkDataRecondsOnRowKeys(tableName: String, keys: Iterator[String], hTable: Table): Array[Result] ={
    val getList = new java.util.ArrayList[Get]
    keys.foreach(key => getList.add(new Get(key.getBytes())))
    hTable.get(getList)
  }

  private def getTable(connectionConfig: Broadcast[HBaseConnectionConfig], tableName: String): Table = {
    val connectionConf = connectionConfig.value
    conf.set(HBaseConnectionConfig.ZookeeperQuorum, connectionConf.quorum)
    conf.setInt(HBaseConnectionConfig.ZookeeperPort, connectionConf.port)

    val table: TableName = TableName.valueOf(tableName)
    val connection = ConnectionFactory.createConnection(conf)
    connection.getTable(table)
  }

  private def getRow(result: Result, schema: StructType, columnFamily: String): Row = {
    val fields: Array[StructField] = schema.fields
    val values: Array[Any] = fields.map(field => {
      val dataType: DataType = field.dataType
      val value = Option(field.name)
        .filter(name => name.equalsIgnoreCase("rowkey"))
        .map(_ => result.getRow)
        .getOrElse(result.getValue(columnFamily.getBytes, field.name.getBytes))
      if(value != null) this.getValueObject(dataType, value) else null
    })

    new GenericRowWithSchema(values, schema)
  }

  private def getValueObject(dataType: DataType, value: Array[Byte]): Any = {
    dataType match {
      case DataTypes.LongType =>
        Bytes.toLong(value)
      case DataTypes.StringType =>
        Bytes.toString(value)
    }
  }
}

object HBaseReadRepository {

  def apply(tableName: String,
           @transient conf: Configuration)(implicit sparkSesion: SparkSession): HBaseReadRepository = {

    val config = HBaseConfiguration.create(conf)
    config.set("mapreduce.outputformat.class","org.apache.hadoop.hbase.mapreduce.TableOutputFormat")
    config.set(TableInputFormat.INPUT_TABLE, tableName)
    config.set(TableOutputFormat.OUTPUT_TABLE, tableName)
    new HBaseReadRepository(config)(sparkSesion)
  }
}
