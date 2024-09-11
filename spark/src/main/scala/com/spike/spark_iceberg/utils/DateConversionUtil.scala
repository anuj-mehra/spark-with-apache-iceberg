// scalastyle:off
package com.spike.spark_iceberg.utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZonedDateTime, ZoneId}

object DateConversionUtil extends Serializable  {

  def convertToEpoch(dateStr: String, zone: String): Long = {
    // Define the input date format
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy")

    // Parse the input date string to a LocalDate
    val localDate = LocalDate.parse(dateStr, dateFormatter)

    // Convert LocalDate to ZonedDateTime using the provided time zone
    val zonedDateTime = ZonedDateTime.of(localDate.atStartOfDay(), ZoneId.of(zone))

    // Convert ZonedDateTime to epoch seconds
    zonedDateTime.toEpochSecond
  }

  def main(args: Array[String]): Unit = {
    val dateStr = "11-Sep-24"  // Example date string
    //val zone = "America/New_York"  // Example time zone

    val zone = "UTC"

    val epochTime = convertToEpoch(dateStr, zone)
    println(s"Epoch time for $dateStr in $zone is: $epochTime")
  }
}
