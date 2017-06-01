package com.github.ralfstuckert.junit.jupiter.extension.mongo;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MongoCleanerConfig.class)
@ExtendWith(MongoCleanupExtension.class)
public @interface MongoCleanup {

    /**
     * @return the entity classes to clean up.
     */
    Class<?>[] value();
}
