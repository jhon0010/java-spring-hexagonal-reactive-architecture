package com.crm.validation.lead.infrastructure.adapter.in.kafka.consumer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumer {
    
    @KafkaListener(topics = "leads", groupId = "leads-consumer")
    public void consume(ConsumerRecord<String, SpecificRecordBase> record) {
        String key = record.key();
        SpecificRecordBase recordValue = record.value();

        log.info("Lead Promoted Event received: {}, with key: {}", recordValue, key);
    }

}
