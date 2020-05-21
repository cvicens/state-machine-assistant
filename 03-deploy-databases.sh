#!/bin/bash

# Environment
. ./00-environment.sh

# Data Base for backend
oc new-app -e POSTGRESQL_USER=luke -ePOSTGRESQL_PASSWORD=secret -ePOSTGRESQL_DATABASE=my_data \
    centos/postgresql-10-centos7 --name=backend-database -n ${PROJECT_NAME}

# Data Base for telegram-bot
oc new-app -e POSTGRESQL_USER=luke -ePOSTGRESQL_PASSWORD=secret -ePOSTGRESQL_DATABASE=my_data \
    centos/postgresql-10-centos7 --name=telegram-bot-database -n ${PROJECT_NAME}

oc label dc/backend-database app.openshift.io/runtime=postgresql --overwrite -n ${PROJECT_NAME} && \
oc label dc/telegram-bot-database app.openshift.io/runtime=postgresql --overwrite -n ${PROJECT_NAME} && \
oc label dc/backend-database app.kubernetes.io/part-of=${APP_NAME} --overwrite -n ${PROJECT_NAME} && \
oc label dc/telegram-bot-database app.kubernetes.io/part-of=${APP_NAME} -n ${PROJECT_NAME} --overwrite

#oc annotate dc/coolstore app.openshift.io/connects-to=coolstore-postgresql --overwrite && \
#oc annotate dc/coolstore app.openshift.io/vcs-uri=https://github.com/RedHat-Middleware-Workshops/cloud-native-workshop-v2m1-labs.git --overwrite && \
#oc annotate dc/coolstore app.openshift.io/vcs-ref=ocp-4.4 --overwrite