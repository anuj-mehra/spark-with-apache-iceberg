// scalastyle:off
package com.spike.spark_iceberg.ingestionframework.fileingestion
import org.rogach.scallop.ScallopConf

class FileValidationCommandLineOptions(arguments: Seq[String]) extends ScallopConf(arguments) {

  val applicationConf = opt[String](short = 'a', required = true)
  val ppCode = opt[String](short = 'a', required = true)
  val subPPCode = opt[String](short = 'a', required = true)
  val trgtSysCd= opt[String](short = 'a', required = true)
  val fileContentCd = opt[String](short = 'a', required = true)
  val srsSysRgnCd= opt[String](short = 'a', required = true)
  val prdTypeCd= opt[String](short = 'a', required = true)
  val trgtSysRgnCd= opt[String](short = 'a', required = true)
  val busDate = opt[String](short = 'a', required = true)
  val processName = opt[String](short = 'a', required = true)
  val processType = opt[String](short = 'a', required = true)
  val fileType = opt[String](short = 'a', required = true)
  val fileFormat = opt[String](short = 'a', required = true)
  val schemaFile= opt[String](short = 'a', required = true)
  val inputFeedFile= opt[String](short = 'a', required = true)
  val referenceFile= opt[String](short = 'a', required = true)
  val recordDelimiter= opt[String](short = 'a', required = true)
  val lineSeparator= opt[String](short = 'a', required = true)
  val subPartitionCol= opt[String](short = 'a', required = true)
  val headerCount= opt[String](short = 'a', required = true)
  val trailerCount= opt[String](short = 'a', required = true)
  val fileCountAdjustValue= opt[String](short = 'a', required = true)
  val writeToParquet= opt[String](short = 'a', required = true)
}
