#!/bin/bash

# Environment
. ./00-environment.sh

cat << EOF | oc -n ${PROJECT_NAME} apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: events-to-bot 
  namespace: "${PROJECT_NAME}"  
data:
  application.properties: |-
    my.message=value-1
    kafka.bootstrap-servers=${CLUSTER_NAME}-kafka-brokers:9092
    kafka.from.topic=${EVENTS_TOPIC_NAME}
    kafka.clientId=kafkaClientHl7ToEvents
    kafka.groupId=kafkaHl7EventsConsumerGroup
    telegram-bot.host=telegram-bot
    telegram-bot.port="8080" 
    logging.level.org.apache.camel=INFO
EOF

./kamel run --configmap=events-to-bot \
  ./integrations/EventsToTelegramBot.java --dev -n ${PROJECT_NAME}