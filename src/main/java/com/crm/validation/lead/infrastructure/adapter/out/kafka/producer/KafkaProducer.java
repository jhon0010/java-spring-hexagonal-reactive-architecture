package com.crm.validation.lead.infrastructure.adapter.out.kafka.producer;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    
    private final String topicName = "leads";
    private KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    
    public KafkaProducer(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLeadToKafka(SpecificRecordBase record) {
        kafkaTemplate.send(topicName, record);
    }

}
