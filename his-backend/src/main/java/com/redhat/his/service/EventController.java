/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.his.service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.streams.KafkaStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/events")
@Component
public class EventController {
	private final static Logger LOGGER = (Logger) LoggerFactory.getLogger(EventController.class);

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");

	private final static String ADT_A04 = "MSH|^~\\&|hl7Integration|hl7Integration|||||ADT^A01|||2.4|\r"
			+ "EVN|A01|20130617154644\r"
			+ "PID|||PATID1234^5^M11||JONES^WILLIAM^A^III||19610615|M-||2106-3|1200 N ELM STREET^^GREENSBORO^NC^27401-1020|GL|(919)379-1212|(919)271-3434~(919)277-3114||S||PATID12345001^2^M10|123456789|9-87654^NC\r"
			+ "NK1|1|Wood^John^^^MR|Father||999-9999\r" + "NK1|2|Jones^Georgie^^^MSS|MOTHER||999-9999\r"
			+ "PV1|1||Location||||||||||||||||261938_6_201306171546|||||||||||||||||||||||||20130617134644|||||||||\r";

	private Long _count = 0L;

	@Autowired
	Producer<Long, String> producer;

	@Autowired
	@Qualifier("eventConsumer")
	Consumer<Long, String> consumer;

	@Value("${kafka.topic}")
	private String topicName;

	@PostConstruct
	public void init() {
		System.out.println(">>> init() with topics = " + topicName);
		Runnable runnable = () -> {
			final int giveUp = 100000;
			int noRecordsCount = 0;

			// Subscribe to Topics
			consumer.subscribe(Collections.singletonList(topicName));

			while (true) {
				final ConsumerRecords<Long, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
				if (consumerRecords.count() == 0) {
					noRecordsCount++;
					if (noRecordsCount > giveUp)
						break;
					else
						continue;
				}
				consumerRecords.forEach(record -> {
					System.out.printf("\n->New message received at EventController! Consumer Record:(%d, %d, %d)\n",
							record.key(), record.partition(), record.offset());
					String event = record.value();
					System.out.println("EVENT: " + event);
				});
				consumer.commitAsync();
			}
			consumer.close();
			System.out.println("DONE");
		};

		Thread thread = new Thread(runnable);
		thread.start();
	}

	@GetMapping
	public ResponseEntity<String> sendEvent(@RequestParam("personalId") String personalId,
			@RequestParam("eventType") String eventType, @RequestParam("eventTrigger") String eventTrigger) {
		// Event newEvent = new Event(eventType, eventTrigger,
		// sdf.format(System.currentTimeMillis()));

		String message = new String(ADT_A04);
		String newEvent =  Base64.getEncoder().encodeToString(message.getBytes());
		ResponseEntity<String> response = _sendToTopic(topicName, newEvent);
        
        return response;
    }
    
    private ResponseEntity<String> _sendToTopic(String topicName, String event) {
		// Prepare message
		ProducerRecord<Long, String> record = new ProducerRecord<>(topicName, _count++, event);

		// Producer
		RecordMetadata metadata = null;
		try {			
			metadata = producer.send(record).get();
			System.out.printf("\nRecord sent to partition %s with offset %s", metadata.partition(), metadata.offset());
		} catch (Exception e) {
			System.out.printf("\nError in sending record", e.getMessage());
			e.printStackTrace();
		} finally {
			producer.flush();
			//producer.close();
		}

		if (metadata != null) {
			return ResponseEntity.ok(String.format("Record sent to partition '%s' with offset '%s'", 
					metadata.partition(),
					metadata.offset()));	
		}
		
		return ResponseEntity.ok(String.format("Error while sending to topic '%s'", topicName));
	}
}
