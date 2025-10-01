package com.crm.validation.lead.infrastructure.adapter.in.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    
    private final String topicName = "leads";

    @Bean
    public NewTopic dealTopic() {
        return new NewTopic(topicName, 1, (short) 1);
    }
}
