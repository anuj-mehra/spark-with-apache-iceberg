package com.spike.spark_iceberg.mapper

import org.apache.spark.sql.types.{LongType, StringType}

class PositionMapping extends Mapping with Serializable {

  register(Mapper(sdpColumname="akey", p1ColumnName="acct_key", dataType=StringType))
  register(Mapper(sdpColumname="posndt", p1ColumnName="posn_as_of_dt", dataType=LongType))

}
