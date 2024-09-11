// scalastyle:off
package com.spike.spark_iceberg.ingestionframework.fileingestion

import com.spike.spark_iceberg.repository.spark.SparkReadRepository
import org.apache.spark.sql.SparkSession

class MetaFileConfig(commandLineOptions: FileValidationCommandLineOptions,
                     sparkReader: SparkReadRepository)(implicit sparkSession: SparkSession) {

  //lazy val metaFileDf: DataFrame = sparkReader.read
}
