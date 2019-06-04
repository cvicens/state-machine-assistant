#!/bin/bash

# Environment
. ./00-environment.sh

cd telegram-bot/

export TELEGRAM_BOT_CUSTOM_PORT=9090
export TELEGRAM_TOKEN="829504574:AAEjoaDiD0118_YFI88g94CI5eIfo7wCnpY"
if [ -z "${TELEGRAM_TOKEN}" ]
then
      echo "\$TELEGRAM_TOKEN is empty! Please fix $0"
      exit 1
fi

#oc port-forward dc/telegram-bot-database 5432:5432 -n ${PROJECT_NAME} &
docker stop telegram-bot-postgres && docker rm telegram-bot-postgres
docker run --name telegram-bot-postgres -e POSTGRES_PASSWORD=secret -e POSTGRES_USER=luke -e POSTGRES_DB=my_data -p 5432:5432 -d postgres

sleep 3

npm start