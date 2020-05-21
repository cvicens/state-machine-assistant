#!/bin/bash

# Environment
. ./00-environment.sh

# Download and install kamel CLI in current path
curl -L -o kamel.tar.gz https://github.com/apache/camel-k/releases/download/${KAMEL_CLIENT_VERSION}/camel-k-client-${KAMEL_CLIENT_VERSION}-${PLATFORM}-64bit.tar.gz
tar xzf kamel.tar.gz ./kamel
rm kamel.tar.gz