#!/bin/bash

# Environment
. ./00-environment.sh

CURRENT_DIR=$(pwd)

oc project ${PROJECT_NAME}

# Deploy backend
cd ${CURRENT_DIR}/backend/
mvn fabric8:deploy -Popenshift -DskipTests

# Labels
oc label dc/backend app.openshift.io/runtime=java --overwrite -n ${PROJECT_NAME} && \
oc label dc/backend app.kubernetes.io/part-of=${APP_NAME} -n ${PROJECT_NAME} --overwrite

oc annotate dc/backend app.openshift.io/connects-to=backend-database --overwrite -n ${PROJECT_NAME} && \
oc annotate dc/backend app.openshift.io/vcs-uri=https://github.com/cvicens/state-machine-assistant.git --overwrite -n ${PROJECT_NAME} && \
oc annotate dc/backend app.openshift.io/vcs-ref=master --overwrite -n ${PROJECT_NAME}

# Deploy frontend
cd ${CURRENT_DIR}/frontend/
npm run openshift

# Labels
oc label dc/frontend app.openshift.io/runtime=nodejs --overwrite -n ${PROJECT_NAME} && \
oc label dc/frontend app.kubernetes.io/part-of=${APP_NAME} -n ${PROJECT_NAME} --overwrite

oc annotate dc/frontend app.openshift.io/connects-to=backend --overwrite -n ${PROJECT_NAME} && \
oc annotate dc/frontend app.openshift.io/vcs-uri=https://github.com/cvicens/state-machine-assistant.git --overwrite -n ${PROJECT_NAME} && \
oc annotate dc/frontend app.openshift.io/vcs-ref=master --overwrite -n ${PROJECT_NAME}
