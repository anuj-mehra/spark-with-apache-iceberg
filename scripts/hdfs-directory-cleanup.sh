#!/usr/bin/env bash
set -e

while getopts p:d: option
do
  case "${option}"
  in
    p) HDFS_PATH=${OPTARG};;
    d) DAYS_TO_KEEP=${OPTARG};;
  esac
done

CURRENT_DATE=`date + '%s'`

# List files in the HDFS directory and get their modification time
hdfs dfs -ls $HDFS_PATH | while read -r line; do
  # Extract the date and time part from the listing
  FILE_DATE=$(echo $line | awk '{print $6" "$7}')
  # Convert file modification date to seconds since epoch
  FILE_DATE_EPOCH=$(date -d "$FILE_DATE" +%s)

  # Calculate the file age in days
  FILE_AGE_DAYS=$(( (CURRENT_DATE - FILE_DATE_EPOCH) / (60*60*24) ))

  # Extract the file path from the listing
  FILE_PATH=$(echo $line | awk '{print $8}')

  # If file age is greater than the specified number of days, delete the file
  if [ $FILE_AGE_DAYS -gt $DAYS ]; then
    echo "Deleting file: $FILE_PATH"
    hdfs dfs -rm -r $FILE_PATH
  fi
done
