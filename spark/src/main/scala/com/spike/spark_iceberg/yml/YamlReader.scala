/*// scalastyle:off
package com.spike.spark_iceberg.yml

import java.nio.file.{Files, Path, Paths}
import scala.collection.JavaConversions.asScalaBuffer
import com.spike.spark_iceberg.yml.YamlProtocol._
import net.jcazevedo.moultingyaml.PimpedString

object YamlReader {

  def read(yamlName: String): YamlMetadataMaster = {

    val yamlMetadataMaster: YamlMetadataMaster =
      Files.readAllLines(toPath(yamlName))
        .toList
        .mkString(System.lineSeparator())
        .parseYaml
        .convertTo[YamlMetadataMaster]

    yamlMetadataMaster
  }

  private def toPath(resourcePath: String): Path = {
    Paths.get(resourcePath)
  }
}*/
