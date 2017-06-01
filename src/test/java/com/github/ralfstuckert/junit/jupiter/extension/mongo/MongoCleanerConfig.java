package com.github.ralfstuckert.junit.jupiter.extension.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoCleanerConfig {

    @Bean
    public MongoCleaner mongoCleaner() {
        return new MongoCleaner();
    }
}
