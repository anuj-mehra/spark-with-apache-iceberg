// scalastyle: off
package com.spike.spark_iceberg.utils

import org.apache.avro.Schema
import org.apache.avro.file.DataFileReader
import org.apache.avro.generic.GenericDatumReader
import java.io.{File, PrintWriter}

object AvroToAvscConverter {

  def extractSchemaFromAvro(avroFilePath: String, outputAvscFilePath: String): Unit = {
    // Open the Avro file
    val avroFile = new File(avroFilePath)
    val datumReader = new GenericDatumReader[Any]()
    val dataFileReader = new DataFileReader[Any](avroFile, datumReader)

    // Extract the schema from the Avro file
    val schema: Schema = dataFileReader.getSchema
    dataFileReader.close()

    // Convert schema to JSON string
    val schemaJson = schema.toString(true)  // 'true' for pretty printing

    // Write the schema to an .avsc file
    val writer = new PrintWriter(new File(outputAvscFilePath))
    writer.write(schemaJson)
    writer.close()

    println(s"Schema extracted and saved to $outputAvscFilePath")
  }

  /*def main(args: Array[String]): Unit = {
    // Example input Avro file path and output .avsc file path
    val avroFilePath = "example.avro"
    val outputAvscFilePath = "example.avsc"

    extractSchemaFromAvro(avroFilePath, outputAvscFilePath)
  }*/
}

