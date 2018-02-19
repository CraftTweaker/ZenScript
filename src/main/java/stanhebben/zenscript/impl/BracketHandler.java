package stanhebben.zenscript.impl;

import java.lang.annotation.*;

/**
 * Marks a bracket handler. The marked class should have an empty constructor.
 *
 * @author Stan Hebben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BracketHandler {
    
    /**
     * Indicates priority. A lower value means a higher priority. Only change if
     * you have issues, default value is 10.
     *
     * @return priority
     */
    int priority() default 10;
}
