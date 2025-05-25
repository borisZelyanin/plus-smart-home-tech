package ru.practicum.aggregator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aggregator")
public class AggregatorProperties {
    private String inputTopic;
    private String outputTopic;
}