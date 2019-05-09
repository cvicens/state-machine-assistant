#!/bin/bash

export PLATFORM="mac" # linux | mac | windows

export PROJECT_NAME="openwhisk-infra"

export MINISHIFT_VERSION="v3.11.0"

export MINISHIFT_PROFILE="camel-k"
export MINISHIFT_MEMORY="5GB"
export MINISHIFT_CPUS="3"
export MINISHIFT_VM_DRIVER="xhyve" # xhyve | virtualbox | kvm
export MINISHIFT_DISK_SIZE="50g"

export PROJECT_NAME="health-assitant"
export CLUSTER_NAME="his-cluster"
export TOPIC_NAME="hl7-events-topic"

export KAMEL_CLIENT_VERSION="0.3.2"