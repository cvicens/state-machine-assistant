#!/bin/bash

# Environment
. ./00-environment.sh

# Data Base for his-backend
oc new-app -e POSTGRESQL_USER=luke -ePOSTGRESQL_PASSWORD=secret -ePOSTGRESQL_DATABASE=my_data \
    openshift/postgresql-92-centos7 --name=his-backend-database

# Data Base for telegram-bot
oc new-app -e POSTGRESQL_USER=luke -ePOSTGRESQL_PASSWORD=secret -ePOSTGRESQL_DATABASE=my_data \
    openshift/postgresql-92-centos7 --name=telegram-bot-database