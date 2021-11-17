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
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: ${CLUSTER_NAME}
spec:
  kafka:
    version: ${KAFKA_VERSION}
    replicas: 3
    listeners:
      - name: plain
        port: 9092
        type: internal
        tls: false
      - name: tls
        port: 9093
        type: internal
        tls: true
      - name: route
        port: 9094
        type: route
        tls: true
    config:
      offsets.topic.replication.factor: 3
      transaction.state.log.replication.factor: 3
      transaction.state.log.min.isr: 2
      log.message.format.version: '2.8'
      inter.broker.protocol.version: '2.8'
    storage:
      type: ephemeral
  kafkaExporter:
    groupRegex: ".*" 
    topicRegex: ".*" 
    resources: 
      requests:
        cpu: 200m
        memory: 64Mi
      limits:
        cpu: 500m
        memory: 128Mi
    readinessProbe: 
      initialDelaySeconds: 15
      timeoutSeconds: 5
    livenessProbe: 
      initialDelaySeconds: 15
      timeoutSeconds: 5
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

# echo "If the next command fails tell cluster-admin to run this for you: oc policy add-role-to-user monitoring-edit ${USER_NAME} -n ${PROJECT_NAME}"

# cat << EOF | oc -n ${PROJECT_NAME} create -f -
# apiVersion: monitoring.coreos.com/v1
# kind: ServiceMonitor
# metadata:
#   name: kafka-monitor
#   labels:
#     k8s-app: kafka-monitor
# spec:
#   endpoints:
#     - interval: 30s
#       port: tcp-prometheus
#   selector:
#     matchLabels:
#       strimzi.io/kind: Kafka
# EOF

