package ru.practicum.ewm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.Conditions;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.logstash.LogstashLogbackSink;

import java.time.Clock;

@Configuration
public class StatsServiceConfig {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public Logbook logbook() {
        final HttpLogFormatter formatter = new JsonHttpLogFormatter();
        final LogstashLogbackSink logstashSink = new LogstashLogbackSink(formatter);
        return Logbook.builder()
                .condition(Conditions.exclude(Conditions.requestTo("/actuator/health")))
                .sink(logstashSink)
                .build();
    }
}
