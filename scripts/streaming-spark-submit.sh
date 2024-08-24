#!/usr/bin/env bash
set -e

export JAVA_HOME=/usr/java/default
export KRB5_CONFIG=/opt/cloudera/KRB5/krb5.conf
export KRB5CCNAME=/tmp/krb5cc_$UID
keytab_filepath=/opt/Cloudera/${USER}.$(hostname -s).keytab
kerberos_principal=${USER}/$(hostname -f)@${KINIT_AD_DOMAIN}

while getopts a:c:t:m:y:r:n:k:s:a:b:q: option
do
  case "${option}"
  in
    a) CONF_BASE_PATH="${OPTARG}";;
    c) RUNNING_CLASS="${OPTARG}";;
    t) JOBNAME="${OPTARG}";;
    m) DRIVER_MEMORY="--driver-memory ${OPTARG}";;
    y) EXECUTOR_MEMORY="--executor-memory ${OPTARG}";;
    r) EXECUTOR_CORES="--executor-cores ${OPTARG}";;
    n) NUM_EXECUTORS="--num-executors ${OPTARG}";;
    k) SPARK_KRYO_BUFFER_MAX="--conf spark.kryoserializer.buffer.max=${OPTARG}";;
    s) SPARK_RPC_MESSAGE_MAXSIZE="--conf spark.kryoserializer.buffer.max=${OPTARG}";;
    w) SPARK_DYNAMIC_ALLOCATION_ENABLED="--conf spark.dynamicAllocation.enabled=${OPTARG}";;
    z) SPARK_DYNAMIC_MAX_EXECUTOR="--conf spark.dynamicAllocation.maxExecutors=${OPTARG}";;
    t) DRIVER_OVERHEAD="--conf spark.driver.memoryOverhead=${OPTARG}";;
    v) EXECUTOR_OVERHEAD="--conf spark.executor.memoryOverhead=${OPTARG}";;
    x) SPARK_JOB_MAX_ATTEMPTS="--conf spark.yarn.maxAppAttempts=${OPTARG}";;
    *) echo "Error: Unsupported flag passed. Check usage."
       usage
       exit 1
       ;;
  esac
done

echo "Application file path => ${CONF_BASE_PATH}/application.conf"
YARN_QUEUE="yarn-queue-name"

FILE_PARAMS="${APP_CONFIG_PATH}/application.conf#app.conf,${YML_FILE_PATH},${APP_CONFIG_PATH}/security.xml#security.xml"
SPARK_DRIVER_EXTRA_JAVA_OPTIONS="-Dlog4j.configuration=log4j.prop"
SPARK_EXECUTOR_EXTRA_JAVA_OPTIONS="-Dlog4j.configuration=log4j.prop"

if [ ${ENV.CATEGORY} == dev ]
then
  RUNNING_JAR_SYMLINK={jar_base_location_symlink}/myapplication-${ENV.CATEGORY}-${ENV.REGION}
else
  RUNNING_JAR_SYMLINK={jar_base_location_symlink}/myapplication
fi

echo "app name on yarn ui will be : ${JOBNAME}"

set +e

  export SPARK_KAFKA_BERSION="0.10"

  spark-submit --name $JOBNAME --class RUNNING_CLASS --master yarn --queue YARN_QUEUE --deploy-mode cluster ${DRIVER_MEMORY} \
    ${EXECUTOR_MEMORY} ${EXECUTOR_CORES} ${NUM_EXECUTORS} ${SPARK_KRYO_BUFFER_MAX} ${SPARK_RPC_MESSAGE_MAXSIZE} ${DRIVER_OVERHEAD} ${EXECUTOR_OVERHEAD} \
    ${SPARK_DYNAMIC_ALLOCATION_ENABLED} ${SPARK_DYNAMIC_MAX_EXECUTOR} \
    ${SPARK_JOB_MAX_ATTEMPTS}
    --conf spark.kryo.referenceTracking=false \
    --conf spark.driver.maxResultSize=2048MB \
    --conf spark.yarn.submit.waitAppCompletion=false \
    --conf spark.streaming.kafka.maxRatePerPartition="${TRANSFORM_MAX_RATE_PER_PARTITION}" \
    --conf spark.streaming.backpressure.enabled=true \
    --conf spark.streaming.backpressure.pid.minRate=10 \
    --conf spark.ui.killEnabled=false \
    --conf spark.logConf=false \
    --conf spark.locality.wait=10 \
    --conf spark.task.maxFailures=30 \
    --conf spark.yarn.maxAppAttempts=4 \
    --conf spark.yarn.am.attemptFailuresValidityInterval=1h \
    --conf spark.yarn.executor.failuresValidityInterval=1h \
    --conf "spark.hadoop.hive.exec.dynamic.partition.mode=nonstrict" \
    --conf "spark.hadoop.hive.exec.dynamic.partition=true" \
    --conf "spark.hadoop.fs.hdfs.impl.disable.cache=true" \
    --conf "spark.sql.session.timeZone=UTC" \
    --conf "spark.yarn.stagingDir={data-directory}/sparkStaging/" \
    --conf "spark.sql.crossJoin.enabled=true" \
    --files ${FILE_PARAMS} \
    --conf ${SPARK_DRIVER_EXTRA_JAVA_OPTIONS} \
    --conf ${SPARK_EXECUTOR_EXTRA_JAVA_OPTIONS} \
    --keytab ${keytab_filepath} \
    --principal ${kerberos_principal} \
    ${RUNNING_JAR_SYMLINK}  -a app.conf


set -e


