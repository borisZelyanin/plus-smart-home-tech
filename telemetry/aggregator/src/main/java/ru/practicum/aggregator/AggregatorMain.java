
package ru.practicum.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AggregatorMain {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorMain.class, args);
        AggregationStarter starter = context.getBean(AggregationStarter.class);
        starter.start();
    }
}
