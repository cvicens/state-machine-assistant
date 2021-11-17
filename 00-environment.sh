#!/bin/bash

SCRIPTPATH="$(cd "$(dirname "$BASH_SOURCE")"; pwd)"

export PLATFORM="linux" # linux | mac

# If on Eclipse CHE
if [ -z "${CHE_WORKSPACE_NAMESPACE}" ]
then
      WORK_USER=$(oc whoami)
else
      WORK_USER=${CHE_WORKSPACE_NAMESPACE}
fi

export KAFKA_VERSION=2.8.0

export PROJECT_NAME="${WORK_USER}-sma"
export CLUSTER_NAME="sma-cluster"

export HL7_EVENTS_TOPIC_NAME="hl7-events-topic"
export EVENTS_TOPIC_NAME="events-topic"

export KAMEL_CLIENT_VERSION="1.6.1"

export APP_NAME="state-machine-app"

# Adds to the PATH the Kamel cli
PATH=$PATH:$SCRIPTPATH