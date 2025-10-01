package com.crm.validation.lead.infrastructure.adapter.in.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.crm.validation.lead.avro.LeadPromotedAvroEvent;

@Component
public class KafkaLeadPromotedProducer {
    
    private final String topicName = "leads";
    private KafkaTemplate<String, LeadPromotedAvroEvent> kafkaTemplate;
    
    public KafkaLeadPromotedProducer(KafkaTemplate<String, LeadPromotedAvroEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLeadToKafka(LeadPromotedAvroEvent lead) {
        kafkaTemplate.send(topicName, lead);
    }

}
