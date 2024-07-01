#!/usr/bin/env bash
set -e

export JAVA_HOME=/usr/java/default
export KRB5_CONFIG=/opt/cloudera/KRB5/krb5.conf
export KRB5CCNAME=/tmp/krb5cc_$UID
keytab_filepath=/opt/Cloudera/${USER}.$(hostname -s).keytab
kerberos_principal=${USER}/$(hostname -f)@${KINIT_AD_DOMAIN}

while getopts j: option
do
  case "${option}"
  in
    j) JOB_NAME=${OPTARG};;
  esac
done


count=$(yarn application -list -appTypes SPARK -appStates RUNNING 2>/dev/null | grep -i ${JOB_NAME} | grep -i ${USER} | wc -l)

if [ "$count" -eq 0 ]; then
  echo "$JOB_NAME is INACTIVE"
  exit 1
fi

exit 0
