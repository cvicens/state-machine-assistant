package com.redhat.his.service;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class KafkaConfig {
	
	@Value("${kafka.bootstrap-servers}")
	private String kafkaBrokers;
	
	@Value("${kafka.clientId}")
	private String clientId;

	@Value("${kafka.groupId}")
	private String groupId;

	@Value("${kafka.topic}")
	private String topicName;

	@Value("${kafka.ssl.truststore.location}")
	private String sslTruststoreLocation;

	@Value("${kafka.ssl.truststore.password}")
	private String sslTruststorePassword;

	@Value("${kafka.ssl.keystore.location}")
	private String sslKeystoreLocation;

	@Value("${kafka.ssl.keystore.password}")
	private String sslKeystorePassword;

	@Bean
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Producer<Long, Event> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
		//props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
		
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());

		props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTruststoreLocation);
		props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslTruststorePassword);

		props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, sslKeystoreLocation);
		props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, sslKeystorePassword);

		//props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");

		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

		
		return new KafkaProducer<>(props);
	}
	
	@Bean(name = "eventConsumer")
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Consumer<Long, Event> createEventConsumer() {
		System.out.println("eventConsumer");
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventDeserializer.class.getName());
		
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

		props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTruststoreLocation);
		props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslTruststorePassword);

		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

		Consumer<Long, Event> consumer = new KafkaConsumer<>(props);
				
		return consumer;
	}
	
	@Bean(name = "wordcountConsumer")
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Consumer<String, Long> createWordcountConsumer() {
		System.out.println("wordcountConsumer");
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

		props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTruststoreLocation);
		props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslTruststorePassword);

		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

		Consumer<String, Long> consumer = new KafkaConsumer<>(props);
				
		return consumer;
	}
}