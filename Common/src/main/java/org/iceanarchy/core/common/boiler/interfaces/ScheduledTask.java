package org.iceanarchy.core.common.boiler.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledTask {
    long delay() default 1L;
}
