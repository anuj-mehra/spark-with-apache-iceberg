// scalastyle:off
package com.spike.spark_iceberg.yml

case class YamlMetadataMaster (yamlMetaData: YamlMetadata)
case class YamlMetadata(domainMetaData: List[YamlDomainMetadata])
case class YamlDomainMetadata(domain: String,
                         sourceFile: YamlFileColumnMetadata,
                         destinationFile: YamlFileColumnMetadata)
case class YamlFileColumnMetadata(fileColumnData: List[Map[String,String]])
