#!/bin/bash

# Environment
. ./00-environment.sh

# Deploy integrations
./kamel run --configmap=hl7-to-events \
  -d camel-gson -d mvn:ca.uhn.hapi:hapi-base:2.3 -d mvn:ca.uhn.hapi:hapi-structures-v24:2.3 \
  ./integrations/HL7ToEvents.java

./kamel run --configmap=events-to-bot ./integrations/EventsToTelegramBot.java
