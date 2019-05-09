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

export FILE_NAME="amq-streams-1.1.0-ocp-install-examples"

#export USERNAME="opentlc-mgr"
#oc adm policy add-cluster-role-to-user cluster-admin ${USERNAME}

echo "1) Download ${FILE_NAME}.zip from https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=66571"
echo "2) Place the file here $(pwd)"

while true; do
    read -p "Do you want to proceed with the installation? " yn
    case $yn in
        [Yy]* ) if [ ! -f ${FILE_NAME}.zip ]; then echo "File ${FILE_NAME}.zip not found!" && continue; fi; break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

unzip ${FILE_NAME}.zip -d ./${FILE_NAME}

oc new-project ${PROJECT_NAME}

sed -i '' "s/namespace: .*/namespace: ${PROJECT_NAME}/" ${FILE_NAME}/install/cluster-operator/*RoleBinding*.yaml

oc apply -n ${PROJECT_NAME} -f ${FILE_NAME}/install/cluster-operator
oc apply -n ${PROJECT_NAME} -f ${FILE_NAME}/examples/templates/cluster-operator

cat << EOF | oc create -n ${PROJECT_NAME} -f -
apiVersion: kafka.strimzi.io/v1alpha1
kind: Kafka
metadata: 
 name: ${CLUSTER_NAME}
spec:
 kafka:
   replicas: 3
   listeners:
     plain: {}
     tls: {}    
     external:
       type: route
   storage:
     type: ephemeral
 zookeeper:
   replicas: 3
   storage:
     type: ephemeral
 entityOperator:
   topicOperator: {}
EOF

cat << EOF | oc -n ${PROJECT_NAME} create -f -
apiVersion: kafka.strimzi.io/v1alpha1
kind: KafkaTopic
metadata:
 name: ${TOPIC_NAME}
 labels:
   strimzi.io/cluster: "${CLUSTER_NAME}"
spec:
 partitions: 3
 replicas: 3
EOF