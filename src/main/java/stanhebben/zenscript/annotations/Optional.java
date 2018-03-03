package stanhebben.zenscript.annotations;


import java.lang.annotation.*;

/**
 * Denotes that this parameter is optional. Inside the script, this parameter
 * may be omitted, in which case it is automatically filled with the default
 * value of that parameter type (false, null or 0). Invalid for @NonNull type.
 * <p>
 * Alternatively, you can provide a default value using either only a String (primitives) or a String value, a class and if needed a String methodName
 * In the latter case it will search for a static method named methodName in the given class that uses a String as input parameter and uses a call to that method as default value.
 *
 * @author Stanneke
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Optional {
    
    /**
     * If this is provided, then the default value will be a call to methodClass#methodName(String value)
     */
    Class<?> methodClass() default Optional.class;
    
    /**
     * Value to be used as default.
     * If omitted, will be the Type's default value (0, null, false)
     * <p>
     * Can either be used on its own (for primitives) or together with the other two members (for all java Objects and primitives)
     */
    String value() default "";
    
    /**
     * Used to change the name of the method for optional method call
     */
    String methodName() default "getValue";
}
