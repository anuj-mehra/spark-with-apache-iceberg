// scalastyle:off
package com.spike.spark_iceberg.ingestionframework.fileingestion

import org.apache.spark.sql.SparkSession

class FileValidationConfig(commandLineOptions: FileValidationCommandLineOptions,
                           appName: String)(implicit sparkSession: SparkSession)
{

  lazy val applicationConf = commandLineOptions.applicationConf.toOption.get
  lazy val ppCode = commandLineOptions.ppCode.toOption.get
  lazy val subPPCode = commandLineOptions.subPPCode.toOption.get
  lazy val trgtSysCd= commandLineOptions.trgtSysCd.toOption.get
  lazy val fileContentCd = commandLineOptions.fileContentCd.toOption.get
  lazy val srsSysRgnCd= commandLineOptions.srsSysRgnCd.toOption.get
  lazy val prdTypeCd= commandLineOptions.prdTypeCd.toOption.get
  lazy val trgtSysRgnCd= commandLineOptions.trgtSysRgnCd.toOption.get
  lazy val busDate = commandLineOptions.busDate.toOption.get
  lazy val processName = commandLineOptions.processName.toOption.get
  lazy val processType = commandLineOptions.processType.toOption.get
  lazy val fileType = commandLineOptions.fileType.toOption.get
  lazy val fileFormat = commandLineOptions.fileFormat.toOption.get
  lazy val schemaFile= commandLineOptions.schemaFile.toOption.get
  lazy val inputFeedFile= commandLineOptions.inputFeedFile.toOption.get
  lazy val referenceFile= commandLineOptions.referenceFile.toOption.getOrElse("")
  lazy val recordDelimiter= commandLineOptions.recordDelimiter.toOption.getOrElse("")
  lazy val lineSeparator= commandLineOptions.lineSeparator.toOption.getOrElse("")
  lazy val subPartitionCol= commandLineOptions.subPartitionCol.toOption.getOrElse("")
  lazy val headerCount= commandLineOptions.headerCount.toOption.get
  lazy val trailerCount= commandLineOptions.trailerCount.toOption.get
  lazy val fileCountAdjustValue= commandLineOptions.fileCountAdjustValue.toOption.get
  lazy val writeToParquet= commandLineOptions.subPartitionCol.toOption.getOrElse("false")

}
