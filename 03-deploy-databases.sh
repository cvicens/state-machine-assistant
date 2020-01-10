#!/bin/bash

# Environment
. ./00-environment.sh

# Data Base for backend
oc new-app -e POSTGRESQL_USER=luke -ePOSTGRESQL_PASSWORD=secret -ePOSTGRESQL_DATABASE=my_data \
    centos/postgresql-10-centos7 --name=backend-database

# Data Base for telegram-bot
oc new-app -e POSTGRESQL_USER=luke -ePOSTGRESQL_PASSWORD=secret -ePOSTGRESQL_DATABASE=my_data \
    centos/postgresql-10-centos7 --name=telegram-bot-database