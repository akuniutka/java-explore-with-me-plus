package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.logstash.LogstashLogbackSink;

import java.time.Clock;

@SpringBootApplication
public class MainService {

    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
    }

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
