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
import java.util.Collections;

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

	private Long _count = 0L;
	
	@Autowired
	Producer<Long,Event> producer;
	
	@Autowired
	@Qualifier("eventConsumer")
	Consumer<Long, Event> consumer;
		
	@Value("${kafka.topic}")
	private String topicName;
	
	@PostConstruct
    public void init() {
		System.out.println(">>> init() with topics = " + topicName);
    	Runnable runnable = () -> {
    		final int giveUp = 100000; int noRecordsCount = 0;
    		
    		// Subscribe to Topics
    		consumer.subscribe(Collections.singletonList(topicName));
    		
            while (true) {
                final ConsumerRecords<Long, Event> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                if (consumerRecords.count() == 0) {
                    noRecordsCount++;
                    if (noRecordsCount > giveUp) break;
                    else continue;
                }
                consumerRecords.forEach(record -> {
                    System.out.printf("\n->New message received at EventController! Consumer Record:(%d, %s, %d, %d)\n",
                            record.key(), record.value(),
							record.partition(), record.offset());
					Event event = record.value();
					System.out.println("EVENT: " + event.getType() + "^" + event.getTrigger() + " " + event.getTimestamp());
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
	public Event sendEvent(
		@RequestParam("personalId") String personalId,
		@RequestParam("eventType") String eventType,
		@RequestParam("eventTrigger") String eventTrigger) {    
        Event newEvent = new Event(eventType, eventTrigger, sdf.format(System.currentTimeMillis()));
        
        _sendToTopic(topicName, newEvent);
        
        return newEvent;
    }
    
    private ResponseEntity<String> _sendToTopic(String topicName, Event event) {
		// Prepare message
		ProducerRecord<Long, Event> record = new ProducerRecord<>(topicName, _count++, event);

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
