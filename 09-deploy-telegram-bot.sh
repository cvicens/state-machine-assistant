#!/bin/bash

# Environment
. ./00-environment.sh

CURRENT_DIR=$(pwd)

# Deploy telegram-bot
cd ${CURRENT_DIR}/telegram-bot/
npm run openshift