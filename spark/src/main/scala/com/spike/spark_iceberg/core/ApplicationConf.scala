// scalastyle:off
package com.spike.spark_iceberg.core

import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import java.nio.file.Paths

class ApplicationConf(ppCode: String,
                      busDate: String,
                      appConfPath: String) extends Serializable{

  lazy val config: Config = ConfigFactory.parseFile(new File(appConfPath)).resolve()

  lazy val projectDirPath = Paths.get("").toAbsolutePath.toString // being used on local machine
  lazy val env = config.getString("env")
  lazy val region = config.getString("region")

  lazy val ymlFilePathBasePath = if(env.equalsIgnoreCase("local")){
    projectDirPath.concat(config.getString("project-base-path")).concat("/yml")
  }else{
    config.getString("project-base-path/yml").concat("yml")
  }

}
