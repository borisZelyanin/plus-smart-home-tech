package ru.practicum.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "analyzer")
public class AnalyzerProperties {
    private String snapshotsTopic;
    private String hubTopic;
}