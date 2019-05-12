#!/bin/bash

# Environment
. ./00-environment.sh

cd telegram-bot/

export TELEGRAM_TOKEN=""
if [ -z "${TELEGRAM_TOKEN}" ]
then
      echo "\$TELEGRAM_TOKEN is empty! Please fix $0"
      exit 1
fi

npm start