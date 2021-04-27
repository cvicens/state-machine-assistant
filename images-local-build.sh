#!/bin/sh

#export GIT_REPO_URL="https://github.com/cvicens/gramola#master"

export PROJECT_PREFIX="sma"

export BACKEND_VERSION=0.0.1
export BACKEND_CONTEXT_DIR="backend"
export BACKEND_SERVICE_NAME="backend"
export BACKEND_IMAGE="${PROJECT_PREFIX}-${BACKEND_SERVICE_NAME}:${BACKEND_VERSION}"

export TELEGRAM_BOT_VERSION=0.0.1
export TELEGRAM_BOT_CONTEXT_DIR="telegram-bot"
export TELEGRAM_BOT_SERVICE_NAME="telegram-bot"
export TELEGRAM_BOT_IMAGE="${PROJECT_PREFIX}-${TELEGRAM_BOT_SERVICE_NAME}:${TELEGRAM_BOT_VERSION}"

export FRONTEND_VERSION=0.0.1
export FRONTEND_CONTEXT_DIR="frontend"
export FRONTEND_SERVICE_NAME="frontend"
export FRONTEND_IMAGE="${PROJECT_PREFIX}-${FRONTEND_SERVICE_NAME}:${FRONTEND_VERSION}"

#export JAVA_BUILDER_IMAGE="registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift"
export JAVA_BUILDER_IMAGE="registry.access.redhat.com/ubi8/openjdk-11:1.3"

#export NODEJS_BUILDER_IMAGE="registry.redhat.io/rhel8/nodejs-12:latest"
export NODEJS_BUILDER_IMAGE="registry.access.redhat.com/ubi8/nodejs-12:1-77"

export USERNAME=atarazana

docker pull ${JAVA_BUILDER_IMAGE}
docker pull ${NODEJS_BUILDER_IMAGE}

s2i build . ${JAVA_BUILDER_IMAGE} --context-dir=${BACKEND_CONTEXT_DIR} ${BACKEND_IMAGE}
docker tag ${BACKEND_IMAGE} quay.io/${USERNAME}/${BACKEND_IMAGE}
docker push quay.io/${USERNAME}/${BACKEND_IMAGE}

s2i build . ${NODEJS_BUILDER_IMAGE} --context-dir=${TELEGRAM_BOT_CONTEXT_DIR} ${TELEGRAM_BOT_IMAGE}
docker tag ${TELEGRAM_BOT_IMAGE} quay.io/${USERNAME}/${TELEGRAM_BOT_IMAGE}
docker push quay.io/${USERNAME}/${TELEGRAM_BOT_IMAGE}

s2i build . ${NODEJS_BUILDER_IMAGE} --context-dir=${FRONTEND_CONTEXT_DIR} ${FRONTEND_IMAGE}
docker tag ${FRONTEND_IMAGE} quay.io/${USERNAME}/${FRONTEND_IMAGE}
docker push quay.io/${USERNAME}/${FRONTEND_IMAGE}

