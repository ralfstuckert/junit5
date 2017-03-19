package com.github.ralfstuckert.junit.jupiter.extension.tempfolder;

import java.lang.annotation.*;

/**
 * Created by Ralf on 08.03.2017.
 */
@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TempFolder {
}
