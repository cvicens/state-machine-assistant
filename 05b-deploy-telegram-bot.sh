#!/bin/bash

# Environment
. ./00-environment.sh

oc project ${PROJECT_NAME}

read -p "PASTE TOKEN: " token
if [[ -z "$token" ]]; then
    printf '%s\n' "You have to paste/type a valid token"
    exit 1
fi

printf "USING TOKEN %s " "$token"
export TELEGRAM_BOT_CUSTOM_PORT=9090
export TELEGRAM_TOKEN=$token
if [ -z "${TELEGRAM_TOKEN}" ]
then
    echo "\$TELEGRAM_TOKEN is empty! Please fix $0"
    exit 1
fi

CURRENT_DIR=$(pwd)

cat << EOF > ${CURRENT_DIR}/telegram-bot/.nodeshift/configmap.yml
kind: ConfigMap 
apiVersion: v1 
metadata:
name: telegram-bot
data:
  token: "${TELEGRAM_TOKEN}"
EOF

# Deploy telegram-bot
cd ${CURRENT_DIR}/telegram-bot/
npm run openshift

# Labels
oc label dc/telegram-bot app.openshift.io/runtime=nodejs --overwrite -n ${PROJECT_NAME} && \
oc label dc/telegram-bot app.kubernetes.io/part-of=${APP_NAME} -n ${PROJECT_NAME} --overwrite

oc annotate dc/telegram-bot app.openshift.io/connects-to=telegram-bot-database --overwrite -n ${PROJECT_NAME} && \
oc annotate dc/telegram-bot app.openshift.io/vcs-uri=https://github.com/cvicens/state-machine-assistant.git --overwrite -n ${PROJECT_NAME} && \
oc annotate dc/telegram-bot app.openshift.io/vcs-ref=master --overwrite -n ${PROJECT_NAME}

