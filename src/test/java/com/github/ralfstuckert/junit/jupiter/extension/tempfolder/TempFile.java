package com.github.ralfstuckert.junit.jupiter.extension.tempfolder;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TempFile {

    String value() default "";

}
