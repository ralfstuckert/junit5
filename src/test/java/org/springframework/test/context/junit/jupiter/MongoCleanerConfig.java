package org.springframework.test.context.junit.jupiter;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

@Configuration
public class MongoCleanerConfig {

    @Bean
    public MongoCleaner mongoCleaner() {
        return new MongoCleaner();
    }
}
