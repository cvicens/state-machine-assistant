#!/bin/bash

# Environment
. ./00-environment.sh

# Download and install kamel CLI in current path
curl -L -o kamel.tar.gz https://github.com/apache/camel-k/releases/download/${KAMEL_CLIENT_VERSION}/camel-k-client-${KAMEL_CLIENT_VERSION}-${PLATFORM}-64bit.tar.gz
tar xzf kamel.tar.gz kamel
rm kamel.tar.gz

# Install kamel in our cluster

WHOAMI=$(oc whoami)
ADMIN_USERS=$(oc get clusterrolebindings -o json | jq -r '.items[] | select(.roleRef.name == "cluster-admin") | .subjects[] | select(.kind == "User") | .name')

for user in ${ADMIN_USERS}
do
    if [[ ${user} == ${WHOAMI} ]]; then echo "Exists"; break; else echo "ERROR: you must be a cluster-admin before proceeding!"; exit 1; fi    # Exists
done

# Install camel-k
echo "Installing camel-k CRDs and the operator"
./kamel install