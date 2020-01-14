# State Machine Assistant

## Intro

Some months ago I started playing with [Camel K](https://github.com/apache/camel-k), I instantanly fell in love with it (and I didn't have any previous experience with Camel), easy to deploy fast development, tons of components, you name it. As it usually happens (not just to me, right ;-) ) we tech people try a new good thing and if we love it... we tend to use it everywhere... ok, that was my case, I started to think of crazy scenarios, but one of them stood out and after some refinement ended up being this project.

> **NOTE:** Ideally you will read and do and see results... and (hopefully) learn some basics to create your own EDA (Event Driven Application). This lab is not really about creating everything from scratch but more about having an example project up and running and then exploring posibilities while learning.

### The idea

The idea came to me after remembering how many times I had to go with my youngest daugher to the Emergency Room (nothing serious but recurrent) and all those times waiting for results, moving to the next stage and informing my wife (who was with my other daughter). Wouldn't if be nice to receive a message to my Telegram App everytime the state of the patient changes? Well, that's the aim of this project: serving as an example EDA (Event Driven Application) running on Kubernetes/OpenShift that sends out notifications via a Telegram Bot whenever the state/status of a patient changes.

In order to give our example EDA a bit more of (ficticious) context, let's imagine there's a Health Information System (HIS) at a hospital called Black Moutain Hospital. This application has a UI where you can change the status of a patient and everytime a change happens a proper HL7 message is sent.

> **NOTE:** this is an over simplified HIS, please health related profesionals don't get too mad at me, I know how complex a real HIS is ;-)

The scenario protrayed by this example application of the **ficticious Black Mountain Hospital** comprises these elements:

* **HIS frontend (Angular JS)** where you can change the status of a patient
* **HIS backend (Spring Boot REST API)** exposing the patient info API, data is persisted in a PostgreSQL Database. This piece sends out HL7 messages to a Kafka topic
* **Integration layer (Camel K)** that translates HL7 events to plain events you can send to a human
* **Telegram Bot (Node JS)** where you can signup with your ID, again data is persisted in a PostgreSQL Database

## Prerequistes

You need access to an OpenShift 4.2+ cluster and be cluster-admin (or request your administrator to install a couple of elements for you). You can also run  your own local 4.x cluster using [CodeReady Containers](https://code-ready.github.io/crc/).

## Scope

In this guide we'll cover:

* the deployment of infrastructure elements in OpenShift: databases, kafka cluster, etc.
* local development of UI, services and also the integration layer
* the deployment of services on OpenShift

## Get yourself ready

Git clone this repository and change dir... the usual.

## Deployment of infrastructure

We need to deploy a couple of databases, Kafka and also Camel K... let's get to it.

### Deploying Kafka 

In order to ease the deployment of Kafka we're going to use the `Red Hat AMQ Operator` (go [here](http://red.ht/operators) to learn more about operators).

> **WARNING:** This task should be run by a cluster administrator

Log in as a cluster admin to your cluster and go to `Operators->Operator Hub`. 

![Operator Hub](./images/ocp-operator-hub-1.png)

In the search field start typing `amq`. Then click on `AMQ Streams`.

![Operator Hub](./images/ocp-operator-hub-2.png)

Click on `Install`.

![Operator Hub](./images/amq-streams-install-1.png)

Leave the default values and click `Subscribe`, as you can see we're going to install the operator so that it's available to all namespaces. This will allow to use the operator as a normal user in any namespace.

![Operator Hub](./images/amq-streams-install-2.png)

Wait until `Status` changes to `InstallSucceeded`.

![Operator Hub](./images/amq-streams-install-3.png)

If status is `InstallSucceeded` you have installed the operator successfully in namespace `openshift-operators`.

![Operator Hub](./images/amq-streams-install-4.png)

Now you could start creating custom resources managed by the AMQ Streams Operator, such as `Kafka`, `Kafka Connect`, etc.


### Deploying the Camel K Operator

As we have exmplained before we need a couple of Camel integrations; to translate HL7 messages coming in to a Kafka topic and another one to send those translated messages to a Telegram Bot. Well, in order to run this Camel integrations (routes) we can do it mannually in a Java project, or use Camel K. For all the reasons mentiones before and more we're going to use an operator the `Camel K Operator`.

> **WARNING:** This task should be run by a cluster administrator

Log in as to your cluster as cluster-admin and go to `Operators->Operator Hub`. 

![Operator Hub](./images/ocp-operator-hub-1.png)

In the search field start typing `camel`. Then click on `Camel K`.

![Operator Hub](./images/camel-k-install-1.png)

Click on `Continue`.

![Operator Hub](./images/camel-k-install-2.png)

Click on `Install`.

![Operator Hub](./images/camel-k-install-3.png)

Leave the default values and click `Subscribe`, as you can see we're going to install the operator so that it's available to all namespaces. This will allow to use the operator as a normal user in any namespace.

![Operator Hub](./images/camel-k-install-4.png)

Wait until `Status` changes to `InstallSucceeded`, if that is the case you have installed the operator successfully in namespace `openshift-operators`.

![Operator Hub](./images/camel-k-install-5.png)


Now you could start creating custom resources managed by the `Camel K Operator`, such as `Integration`, `Build`, etc.

### Deploying Kafka using the AMQ Streams Operator

As we mentioned before we need a couple of Topics, one for HL7 events and another one for translated events... and of course we need a Kafka cluster to support them.

> **NOTE:** This task and the next ones don't require special permissions apart from being able to create namespaces, deployments, PODs, etc.

We have prepared a set of numbered shell scripts, please have a look to the one numbered `00` where some base environment variables are set. You may need to change the project name to be sure it's unique in your cluster...

Set the environment any time by doing this. Please run the next command, we'll need to use $PROJECT_NAME environemt variable later:

```sh
. ./00-environment.sh
```

In this step we will run `./01-deploy-kafka.sh`, please have a look to this script, there are a couple of important bits there.

**First**, it creates a project to hold all the elements. Next is the excerpt 

```sh
oc new-project ${PROJECT_NAME}
```

> **<span style="color:red">IMPORTANT:</span>** if you run the scripts in order (and you don't create another project in between), the default project will be automatically set to the project create, that is the one set by $PROJECT_NAME environment varible, see `00-environment.sh`. If you have created another project or just want to be sure the default project is set correctly, please use: `oc project`. If you need to set the default project back to $PROJECT_NAME do this: `. ./00-environment.sh && oc project $PROJECT_NAME` 

**Second**, it also creates a Custom Resource (CR) of type `Kafka` that defines a Kafka cluster with 3 replicas, three listeners, plain, secure and https based (external). And a couple of CRs of type `KafkaTopic` for each of the kafka topics we need.

> **NOTE 1:** The `AMQ Streams Operator` reacts to the creation/update/delete of a set of Custom Resource Definitions, go [here](https://access.redhat.com/documentation/en-us/red_hat_amq/7.3/html/using_amq_streams_on_openshift_container_platform/getting-started-str#cluster-operator-str) for further details

> **NOTE 2:** The **external listener** is needed only while running the backend logic locally because the kafka cluster is running in OpenShift. In general this external listener is not needed when the logic run in the same cluster as the kafka cluster.

Now please run the script.

> **WARNING:** Be sure you're logged in, if unsure run `oc whoami`

```sh
./01-deploy-kafka.sh
```

To monitor the status of the deployment you can run the next command.

> As you can see, there are 3 replicas both for the kafka cluster and for the zookeeper cluster, all of them are running.

```sh
$ oc get pod -n $PROJECT_NAME | grep state-machine-cluster
state-machine-cluster-entity-operator-55d6f79ccf-dckht   3/3     Running     16         5d15h
state-machine-cluster-kafka-0                            2/2     Running     11         5d15h
state-machine-cluster-kafka-1                            2/2     Running     11         5d15h
state-machine-cluster-kafka-2                            2/2     Running     11         5d15h
state-machine-cluster-zookeeper-0                        2/2     Running     8          5d15h
state-machine-cluster-zookeeper-1                        2/2     Running     8          5d15h
state-machine-cluster-zookeeper-2                        2/2     Running     8          5d15h
```

Another test you can run, this one to check if our topics were created properly.

```sh
$ oc rsh -n $PROJECT_NAME state-machine-cluster-kafka-0 bin/kafka-topics.sh --list --bootstrap-server localhost:9092
Defaulting container name to kafka.
Use 'oc describe pod/state-machine-cluster-kafka-0 -n state-machine-assistant' to see all of the containers in this pod.
OpenJDK 64-Bit Server VM warning: If the number of processors is expected to increase from one, then you should configure the number of parallel GC threads appropriately using -XX:ParallelGCThreads=N
__consumer_offsets
events-topic
hl7-events-topic
```

### Install Camel K CLI

This step is quite easy... it only requires to download the Camel K CLI binary, `kamel`.

Please run this script.

> **NOTE:** open the script `00-environment.sh` if you want to change the cli version

```sh
./02-install-camel-k-cli.sh
```

This script leaves the `kamel` binary in the same directory where the script is run... so it's not in your *PATH* so be sure to execute it like this `./kamel` or add it to you *PATH*.

### Deploying the HIS Backend and Telegram Bot databases

We're going to use `oc new-app` commands to deploy two single node PostgreSQL databases, one for the HIS backend and another one for the Telegram Bot.

If you have a look to script `03` you'll find the next couple of commands, these are normal `oc new-app` commands that create the needed descriptors to run an OCI image `centos/postgresql-10-centos7`. 

Pay attention to the following:

1. We are deploying the same image twice because we need two databases
2. The name given to the application is different: backend-database vs telegram-bot-database
3. Finally, there are a couple of environment variables that set user and password with the same values for both databases. Yes, not very secure... but remember this is for development purposes

```sh
oc new-app -e POSTGRESQL_USER=luke -ePOSTGRESQL_PASSWORD=secret -ePOSTGRESQL_DATABASE=my_data \
    centos/postgresql-10-centos7 --name=backend-database

oc new-app -e POSTGRESQL_USER=luke -ePOSTGRESQL_PASSWORD=secret -ePOSTGRESQL_DATABASE=my_data \
    centos/postgresql-10-centos7 --name=telegram-bot-database
```

Now run the script.

```sh
./03-deploy-databases.sh
```

Check the status of the deployment with this command. Eventually you should see something like this where the status of our database pods is `Running`, like `backend-database-1-ttq2b` in this example output.

```sh
$ oc get pod -n $PROJECT_NAME | grep database
backend-database-1-deploy                                0/1     Completed   0          5m
backend-database-1-ttq2b                                 1/1     Running     4          4m
telegram-bot-database-1-7qv4d                            1/1     Running     4          4m
telegram-bot-database-1-deploy                           0/1     Completed   0          5m
```

## Preparing the development environment

One of the goals of the lab is to help you with the local development stage. For instance, regarding the HIS backend, our Spring Boot service (Java) needs to be able to connect to an HTTPS listener, hence you need the root CA certificate related to that listener added as a trusted CA to a keystore.

To do so, we have prepared this script `./04-prepare-development-env.sh`. It basically does the following:

*Extracts the root CA cert from a secret*

```sh 
oc extract secret/${CLUSTER_NAME}-cluster-ca-cert -n ${PROJECT_NAME} --keys=ca.crt --to=- > src/main/resources/ca.crt
```

*Adds it to a Java Keystore*

```sh
keytool -delete -alias root -keystore src/main/resources/keystore.jks -storepass password -noprompt
keytool -import -trustcacerts -alias root -file src/main/resources/ca.crt -keystore src/main/resources/keystore.jks -storepass password -noprompt
```

How does the Spring Boot application know how to 
kafka.ssl.truststore.location = src/main/resources/keystore.jks
kafka.ssl.truststore.password = password

kafka.ssl.keystore.location = src/main/resources/keystore.jks
kafka.ssl.keystore.password = password
Later we'll explain 

===========================

Wait until kafka cluster is ready ==> XYZ

Prepare development environment
 - It needs the cluster deployed to get cert and create backend/src/main/resources/keystore.jks


Use botfather to create your bot
Run Telegram bot, the script runs postgres with docker just to check locally that everythig works
Find your bot and start taling to it... try /signup XYZ then /update ZYX, finally /quit

Run HIS backend in a new terminal
- runs H2 database so HIS front end will call this version also locally

Run our camel-k integration (run in OCP and connects with kafka using the non secure port 9092) in a new tab
 - should see this -==>   [Camel (camel-k) thread #2 - KafkaConsumer[hl7-events-topic]] Fetcher - [Consumer clientId=consumer-1, groupId=kafkaHisToBotConsumerGroup] Resetting offset for partition hl7-events-topic-1 to offset 0.


Run HIS front end in a new terminal
- Open browser at :4200 when you see this ==> [0] [HPM] Proxy created: [ '/server.json' ]  ->  http://localhost:8090
[0] [HPM] Proxy created: [ '/api/patients', '/api/patients/' ]  ->  http://localhost:8080
[0] 
[0] Date: 2019-05-12T15:34:38.088Z
[0] Hash: 5d47d15fac7ae494bd8f
[0] Time: 12175ms
[0] chunk {es2015-polyfills} es2015-polyfills.js, es2015-polyfills.js.map (es2015-polyfills) 284 kB [initial] [rendered]
[0] chunk {main} main.js, main.js.map (main) 35.1 kB [initial] [rendered]
[0] chunk {polyfills} polyfills.js, polyfills.js.map (polyfills) 236 kB [initial] [rendered]
[0] chunk {runtime} runtime.js, runtime.js.map (runtime) 6.08 kB [entry] [rendered]
[0] chunk {styles} styles.js, styles.js.map (styles) 345 kB [initial] [rendered]
[0] chunk {vendor} vendor.js, vendor.js.map (vendor) 7.09 MB [initial] [rendered]
[0] ℹ ｢wdm｣: Compiled successfully.


Now you can stop the telegram-bot and deploy it using 09-deploy-... you have to deploy it in order to be reachable fro the camel-k integration which is running 'in' the cluster...

You can make changes to your code in backend, frontend, integrations...

Then when you're happy just deploy it all with 10-deploy


 
