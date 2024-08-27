// scalastyle:off
package com.spike.spark_iceberg.core

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path


// DECRYPTED_JCEKS_PASSWORD set in shell scrupt is used as password for the jceks file.
class CredentialProvider extends Serializable{

  def getPassword(envName: String, credStoreKey: String): String = {
    if("local".equalsIgnoreCase(envName)){
      sys.env.get(credStoreKey).orNull
    }else{
      val configuration = new Configuration()
      configuration.addResource(new Path("security.xml"))
      configuration.getPassword(credStoreKey).mkString
    }
  }
}
