#!/bin/bash

# Environment
. ./00-environment.sh

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
