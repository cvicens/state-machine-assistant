#!/bin/bash

# Environment
. ./00-environment.sh

cd backend/

oc extract secret/${CLUSTER_NAME}-cluster-ca-cert -n ${PROJECT_NAME} --keys=ca.crt --to=- > src/main/resources/ca.crt

keytool -delete -alias root -keystore src/main/resources/keystore.jks -storepass password -noprompt
keytool -import -trustcacerts -alias root -file src/main/resources/ca.crt -keystore src/main/resources/keystore.jks -storepass password -noprompt

cd ../frontend

npm install

cd ../telegram-bot

npm install