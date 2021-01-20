#!/bin/bash

# Environment
. ./00-environment.sh

cd telegram-bot/

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

if [ -z "${CHE_PROJECTS_ROOT}" ]
then
      # Using a tunnel the database running in OpenShift
      oc port-forward $(oc get pod -l app=telegram-bot-database -o=jsonpath='{.items[0].metadata.name}' -n $PROJECT_NAME) -n $PROJECT_NAME 5432:5432 &      
else
      export DB_HOST=telegram-bot-database.${PROJECT_NAME}
fi

# Using docker to run a database
#docker stop telegram-bot-postgres && docker rm telegram-bot-postgres
#docker run --name telegram-bot-postgres -e POSTGRES_PASSWORD=secret -e POSTGRES_USER=luke -e POSTGRES_DB=my_data -p 5432:5432 -d postgres

npm start

