kamel run -d camel-http4 -d camel-kafka -d camel-gson -d mvn:ca.uhn.hapi:hapi-base:2.3 -d mvn:ca.uhn.hapi:hapi-structures-v24:2.3 -d camel-hl7 -d camel-fhir -d mvn:ca.uhn.hapi.fhir:hapi-fhir-base:3.7.0 -d mvn:ca.uhn.hapi.fhir:hapi-fhir-structures-dstu3:3.7.0 -p kafka.bootstrap-servers=his-cluster-kafka-brokers:9092 -p kafka.topic=hl7-events-topic -p kafka.clientId=kafkaClientHisToBot -p kafka.groupId=kafkaHisToBotConsumerGroup -p telegram-bot.host=telegram-bot -p telegram-bot.port=8080 -p logging.level.org.apache.camel=INFO ./HisToBot.java --dev

kamel run -d camel-http4 -d camel-kafka -d camel-gson -d mvn:ca.uhn.hapi:hapi-base:2.3 -d mvn:ca.uhn.hapi:hapi-structures-v24:2.3 -d camel-hl7 -p kafka.bootstrap-servers=his-cluster-kafka-brokers:9092 -p kafka.topic=hl7-events-topic -p kafka.clientId=kafkaClientHisToBot -p kafka.groupId=kafkaHisToBotConsumerGroup -p telegram-bot.host=telegram-bot -p telegram-bot.port=8080 -p logging.level.org.apache.camel=INFO ./HisToBot.java --dev




//from("timer:clock?period=5s")




mvn archetype:generate -B \
      -DarchetypeGroupId=io.fabric8.archetypes \
      -DarchetypeArtifactId=cdi-camel-archetype \
      -DgroupId=myf8 \
      -DartifactId=d2 \
      -DarchetypeVersion=2.2.101