package stanhebben.zenscript.annotations;

import java.lang.annotation.*;

/**
 * Used to indicate an exposed constructor.
 * <p>
 * @author kindlich
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface ZenConstructor {}
