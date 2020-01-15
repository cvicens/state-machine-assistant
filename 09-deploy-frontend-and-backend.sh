#!/bin/bash

# Environment
. ./00-environment.sh

CURRENT_DIR=$(pwd)

# Deploy backend
cd ${CURRENT_DIR}/backend/
mvn fabric8:deploy -Popenshift -DskipTests

# Deploy frontend
cd ${CURRENT_DIR}/frontend/
npm run openshift

