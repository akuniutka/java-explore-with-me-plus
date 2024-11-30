package ru.practicum.ewm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.logstash.LogstashLogbackSink;

import java.time.Clock;

@Configuration
public class MainServiceConfig {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public Logbook logbook() {
        final HttpLogFormatter formatter = new JsonHttpLogFormatter();
        final LogstashLogbackSink logstashsink = new LogstashLogbackSink(formatter);
        return Logbook.builder()
                .sink(logstashsink)
                .build();
    }
}
