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

#docker run --name some-postgres -e POSTGRES_PASSWORD=secret -e POSTGRES_USER=luke -e POSTGRES_DB=my_data -p 5432:5432 -d postgres

npm start