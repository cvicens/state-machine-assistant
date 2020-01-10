#!/bin/bash

# https://access.redhat.com/documentation/en-us/red_hat_amq/7.2/html-single/using_amq_streams_on_openshift_container_platform/index

# http://strimzi.io/quickstarts/okd/
# https://developers.redhat.com/blog/2018/10/29/how-to-run-kafka-on-openshift-the-enterprise-kubernetes-with-amq-streams/
# https://developers.redhat.com/products/amq/overview/
# https://developers.redhat.com/blog/2018/05/31/introducing-the-kafka-cdi-library/
# https://developers.redhat.com/blog/2018/07/16/smart-meter-streams-kafka-openshift/
# https://developers.redhat.com/blog/2018/10/15/eventflow-event-driven-microservices-on-openshift-part-1/
# https://developers.redhat.com/blog/2018/05/07/announcing-amq-streams-apache-kafka-on-openshift/

# Environment
. ./00-environment.sh

oc new-project ${PROJECT_NAME}

cat << EOF | oc create -n ${PROJECT_NAME} -f -
apiVersion: kafka.strimzi.io/v1beta1
kind: Kafka
metadata:
  name: ${CLUSTER_NAME}
spec:
  kafka:
    version: 2.3.0
    replicas: 3
    listeners:
      plain: {}
      tls: {}
      external:
       type: route
    config:
      offsets.topic.replication.factor: 3
      transaction.state.log.replication.factor: 3
      transaction.state.log.min.isr: 2
      log.message.format.version: '2.3'
    storage:
      type: ephemeral
  zookeeper:
    replicas: 3
    storage:
      type: ephemeral
  entityOperator:
    topicOperator: {}
    userOperator: {}

EOF

cat << EOF | oc -n ${PROJECT_NAME} create -f -
apiVersion: kafka.strimzi.io/v1beta1
kind: KafkaTopic
metadata:
  name: ${HL7_EVENTS_TOPIC_NAME}
  labels:
    strimzi.io/cluster: "${CLUSTER_NAME}"
spec:
  partitions: 3
  replicas: 3
  config:
    retention.ms: 604800000
    segment.bytes: 1073741824
EOF

cat << EOF | oc -n ${PROJECT_NAME} create -f -
apiVersion: kafka.strimzi.io/v1beta1
kind: KafkaTopic
metadata:
  name: ${EVENTS_TOPIC_NAME}
  labels:
    strimzi.io/cluster: "${CLUSTER_NAME}"
spec:
  partitions: 3
  replicas: 3
  config:
    retention.ms: 604800000
    segment.bytes: 1073741824
EOF