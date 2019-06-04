#!/bin/bash

# Environment
. ./00-environment.sh

CURRENT_DIR=$(pwd)

# Deploy his-backend
cd ${CURRENT_DIR}/his-backend/
mvn fabric8:deploy -Popenshift -DskipTests

# Deploy his-frontend
cd ${CURRENT_DIR}/his-frontend/
npm run openshift

