package com.redhat.his.service;

import java.time.Duration;
import java.util.Base64;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final static String ADMISSION = "admission";
    private final static String TRIAGE = "triage";
    private final static String DIAGNOSIS = "diagnosis";
    private final static String DISCHARGE = "discharge";

    private final static String ADT_A04 = 
        "MSH|^~\\&|ADT1|MCM|LABADT|MCM|198808181126|SECURITY|ADT^A04|MSG00001|P|2.4\r"
        + "EVN|A04|20050110045502|||||\r"
        + "PID||${PATIENT_ID}|${PERSONAL_ID}||${LAST_NAME}^${FIRST_NAME}||19610615|M||2106-3|1200 N ELM STREET^^GREENSBORO^NC^27401-1020|GL|(919)379-1212|(919)271-3434~(919)277-3114||S||${PERSONAL_ID}5001^2^M10|123456789|9-87654^NC\r"
        + "PV1|1|I|ER^^^^^^B|E|||37^MARTINEZ^JOHN^^^^^^AccMgr^^^^CI|||01||||1|||37^MARTINEZ^JOHN^^^^^^AccMgr^^^^CI|2|40007716^^^AccMgr^VN|4|||||||||||||||||||1||G|||20050110045253||||||\r"
        + "AL1|1||^PENICILLIN||PRODUCES HIVES~RASH\r"
        + "AL1|2||^CAT DANDER\r"
        + "PR1|2234|M11|111^CODE151|COMMON PROCEDURES|198809081123\r"
        + "ROL|45^RECORDER^ROLE MASTER LIST|AD|CP|KATE^SMITH^ELLEN|199505011201\r"
        + "GT1|1122|1519|JOHN^GATES^A\r"
        + "IN1|001|A357|1234|BCMD|||||132987\r"
        + "IN2|ID1551001|SSN12345678\r";
            
    private final static String ADT_A08_1 = 
        "MSH|^~\\&|ADT1|MCM|LABADT|MCM|198808181126|SECURITY|ADT^A08|MSG00001|P|2.4\r"
        + "EVN|A08|20050110045502|||||\r"
        + "PID||${PATIENT_ID}|${PERSONAL_ID}||${LAST_NAME}^${FIRST_NAME}||19610615|M||2106-3|1200 N ELM STREET^^GREENSBORO^NC^27401-1020|GL|(919)379-1212|(919)271-3434~(919)277-3114||S||${PERSONAL_ID}5001^2^M10|123456789|9-87654^NC\r"
        + "PV1|1|I|ER^^^^^^B|U|||37^MARTINEZ^JOHN^^^^^^AccMgr^^^^CI|||01||||1|||37^MARTINEZ^JOHN^^^^^^AccMgr^^^^CI|2|40007716^^^AccMgr^VN|4|||||||||||||||||||1||G|||20050110045253||||||\r"
        + "GT1|1122|1519|JOHN^GATES^A\r"
        + "IN1|001|A357|1234|BCMD|||||132987\r"
        + "IN2|ID1551001|SSN12345678\r";
    
    private final static String ADT_A08_2 = 
        "MSH|^~\\&|ADT1|MCM|LABADT|MCM|198808181126|SECURITY|ADT^A08|MSG00001|P|2.4\r"
        + "EVN|A08|20050110045502|||||\r"
        + "PID||${PATIENT_ID}|${PERSONAL_ID}||${LAST_NAME}^${FIRST_NAME}||19610615|M||2106-3|1200 N ELM STREET^^GREENSBORO^NC^27401-1020|GL|(919)379-1212|(919)271-3434~(919)277-3114||S||${PERSONAL_ID}5001^2^M10|123456789|9-87654^NC\r"
        + "PV1|1|I|ER^^^^^^B|U|||37^MARTINEZ^JOHN^^^^^^AccMgr^^^^CI|||01||||1|||37^MARTINEZ^JOHN^^^^^^AccMgr^^^^CI|2|40007716^^^AccMgr^VN|4|||||||||||||||||||1||G|||20050110045253||||||\r"
        + "GT1|1122|1519|JOHN^GATES^A\r"
        + "DG1|1|I9|71596^OSTEOARTHROS NOS-L/LEG ^I9|OSTEOARTHROS NOS-L/LEG ||A|\r"
        + "IN1|001|A357|1234|BCMD|||||132987\r"
        + "IN2|ID1551001|SSN12345678\r";

    private final static String ADT_A03 = 
        "MSH|^~\\&|ADT1|MCM|LABADT|MCM|198808181126|SECURITY|ADT^A03|MSG00001|P|2.4\r"
        + "EVN|A03|20050112154642|||||\r"
        + "PID||${PATIENT_ID}|${PERSONAL_ID}||${LAST_NAME}^${FIRST_NAME}||19610615|M||2106-3|1200 N ELM STREET^^GREENSBORO^NC^27401-1020|GL|(919)379-1212|(919)271-3434~(919)277-3114||S||${PERSONAL_ID}5001^2^M10|123456789|9-87654^NC\r"
        + "PV1|1|I|ER^^^^^^B|U|||37^MARTINEZ^JOHN^^^^^^AccMgr^^^^CI|||01||||1|||37^MARTINEZ^JOHN^^^^^^AccMgr^^^^CI|2|40007716^^^AccMgr^VN|4|||||||||||||||||||1||G|||20050110045253||||||\r";

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
					System.out.printf("\n->New message received at EventService! Consumer Record:(%d, %d, %d)\n",
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
    
    public String sendEvent(Patient patient, String stage) {
        String messageTemplate = ADT_A04;
        switch(stage) {
            case ADMISSION:
                messageTemplate = ADT_A04;
                break;
            case TRIAGE:
                messageTemplate = ADT_A08_1;
                break;
            case DIAGNOSIS:
                messageTemplate = ADT_A08_2;
                break;
            case DISCHARGE:
                messageTemplate = ADT_A03;
                break;
            default:
                messageTemplate = ADT_A04;
        }

        String message = prepareMessage(messageTemplate, patient);
        String newEvent = Base64.getEncoder().encodeToString(message.getBytes());
        String response = sendToTopic(topicName, newEvent);
        
        return response;
    }

    private String prepareMessage(String message, Patient patient) {
        return message.replace("${FIRST_NAME}", patient.getFirstName())
            .replace("${LAST_NAME}", patient.getLastName())
            .replace("${PATIENT_ID}", patient.getPatientId().toString())
            .replace("${PERSONAL_ID}", patient.getPersonalId());
    }

    private String sendToTopic(String topicName, String event) {
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
			return String.format("Record sent to partition '%s' with offset '%s'", 
					metadata.partition(),
					metadata.offset());	
		}
		
		return String.format("Error while sending to topic '%s'", topicName);
	}
}