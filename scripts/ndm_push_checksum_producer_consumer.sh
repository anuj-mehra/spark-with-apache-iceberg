#!/usr/bin/env bash

while getopts a:c:t:m:y:r:n:k:s:a:b:q: option
do
  case "${option}"
  in
    s) source_location="${OPTARG}";;
    f) source_file_name="${OPTARG}";;
    t) target_location="${OPTARG}";;
    l) target_file_name="${OPTARG}";;
    h) hostname="${OPTARG}";;
    o) operation="${OPTARG}";;
  esac
done

function ndm_variables_set(){

}

copy_file() {

  source_location=$1
  source_file_name=$2
  target_location=$3
  target_file_name=$4
  hostname=$5

  #Initialize copy status
  copy_status=1


}