package stanhebben.zenscript.type.natives;

import stanhebben.zenscript.annotations.*;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;

/**
 * @author Stan
 */
public class JavaMethod implements IJavaMethod {

    public static final int PRIORITY_INVALID = -1;
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_HIGH = 3;
    private final Method method;
    public final boolean returnsSelf;
    private final ZenType[] parameterTypes;
    private final boolean[] optional;
    private final ZenType returnType;

    public JavaMethod(Method method, ITypeRegistry types) {
        
        this.method = method;
        this.returnsSelf = method.getDeclaredAnnotationsByType(ReturnsSelf.class).length != 0;

        returnType = types.getType(method.getGenericReturnType());
        parameterTypes = new ZenType[method.getParameterTypes().length];
        optional = new boolean[parameterTypes.length];
        for(int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = types.getType(method.getGenericParameterTypes()[i]);
            optional[i] = false;
            for(Annotation annotation : method.getParameterAnnotations()[i]) {
                if(annotation instanceof Optional) {
                    optional[i] = true;
                }
            }
        }
        boolean lastOptional = false;
        for(int i = 0; i < optional.length; i++) {
            boolean opt = optional[i];
            if(lastOptional && !opt) {
                System.err.println("All optionals need to be placed at the end of the method declaration: " + method.toGenericString() + "! Setting last parameters to optional");
                optional[i] = lastOptional = true;
                //throw new IllegalArgumentException("All optionals need to be placed at the end of the method declaration: " + method.toGenericString());
            } else {
                lastOptional = opt;
            }
        }
    }

    public static IJavaMethod get(ITypeRegistry types, Class cls, String name, Class... parameterTypes) {
        try {
            Method method = cls.getMethod(name, parameterTypes);
            if(method == null) {
                throw new RuntimeException("method " + name + " not found in class " + cls.getName());
            }
            return new JavaMethod(method, types);
        } catch(NoSuchMethodException ex) {
            throw new RuntimeException("method " + name + " not found in class " + cls.getName(), ex);
        } catch(SecurityException ex) {
            throw new RuntimeException("method retrieval not permitted", ex);
        }
    }

    public static IJavaMethod getStatic(String owner, String name, ZenType returnType, ZenType... arguments) {
        return new JavaMethodGenerated(true, false, false, owner, name, returnType, arguments, new boolean[arguments.length]);
    }

    public static IJavaMethod get(ITypeRegistry types, Method method) {
        return new JavaMethod(method, types);
    }

    public static IJavaMethod select(boolean doStatic, List<IJavaMethod> methods, IEnvironmentGlobal environment, Expression... arguments) {
        int bestPriority = PRIORITY_INVALID;
        IJavaMethod bestMethod = null;
        boolean isValid = false;

        for(IJavaMethod method : methods) {
            if(method.isStatic() != doStatic)
                continue;

            int priority = method.getPriority(environment, arguments);
            if(priority == bestPriority) {
                isValid = false;
            } else if(priority > bestPriority) {
                isValid = true;
                bestMethod = method;
                bestPriority = priority;
            }
        }

        return isValid ? bestMethod : null;
    }

    public static ZenType[] predict(List<IJavaMethod> methods, int numArguments) {
        ZenType[] results = new ZenType[numArguments];
        boolean[] ambiguous = new boolean[numArguments];
        for(IJavaMethod method : methods) {
            if(method.accepts(numArguments)) {
                ZenType[] parameterTypes = method.getParameterTypes();
                for(int i = 0; i < numArguments; i++) {
                    if(i >= parameterTypes.length - 1 && method.isVarargs()) {
                        if(numArguments == parameterTypes.length) {
                            ambiguous[i] = true;
                        } else if(numArguments > parameterTypes.length) {
                            if(results[i] != null) {
                                ambiguous[i] = true;
                            } else {
                                results[i] = ((ZenTypeArray) parameterTypes[parameterTypes.length - 1]).getBaseType();
                            }
                        }
                    } else {
                        if(results[i] != null) {
                            ambiguous[i] = true;
                        } else {
                            results[i] = parameterTypes[i];
                        }
                    }
                }
            }
        }

        for(int i = 0; i < results.length; i++) {
            if(ambiguous[i]) {
                results[i] = null;
            }
        }

        return results;
    }

    public static Expression[] rematch(ZenPosition position, IJavaMethod method, IEnvironmentGlobal environment, Expression... arguments) {
        ZenType[] parameterTypes = method.getParameterTypes();

        // small optimization - don't run through this all if not necessary
        if(arguments.length == 0 && parameterTypes.length == 0) {
            return arguments;
        }

        Expression[] result = new Expression[method.getParameterTypes().length];
        for(int i = arguments.length; i < method.getParameterTypes().length; i++) {
            result[i] = parameterTypes[i].defaultValue(position);
        }

        int doUntil = parameterTypes.length;
        if(method.isVarargs()) {
            doUntil = parameterTypes.length - 1;
            ZenType paramType = parameterTypes[parameterTypes.length - 1];
            ZenType baseType = ((ZenTypeArray) paramType).getBaseType();

            if(arguments.length == parameterTypes.length) {
                ZenType argType = arguments[arguments.length - 1].getType();

                if(argType.equals(paramType)) {
                    result[arguments.length - 1] = arguments[arguments.length - 1];
                } else if(argType.equals(baseType)) {
                    result[arguments.length - 1] = new ExpressionArray(position, (ZenTypeArrayBasic) paramType, arguments[arguments.length - 1]);
                } else if(argType.canCastImplicit(paramType, environment)) {
                    result[arguments.length - 1] = arguments[arguments.length - 1].cast(position, environment, paramType);
                } else {
                    result[arguments.length - 1] = new ExpressionArray(position, (ZenTypeArrayBasic) paramType, arguments[arguments.length - 1]).cast(position, environment, paramType);
                }
            } else if(arguments.length > parameterTypes.length) {
                int offset = parameterTypes.length - 1;
                Expression[] values = new Expression[arguments.length - offset];
                for(int i = 0; i < values.length; i++) {
                    values[i] = arguments[offset + i].cast(position, environment, baseType);
                }
                result[offset] = new ExpressionArray(position, (ZenTypeArrayBasic) paramType, values);
            }
        }

        for(int i = arguments.length; i < doUntil; i++) {
            if(method instanceof JavaMethod) {
                JavaMethod javaMethod = (JavaMethod) method;
                result[i] = getOptionalValue(position, javaMethod, i, environment);
            } else
                result[i] = parameterTypes[i].defaultValue(position);
        }
        for(int i = 0; i < Math.min(arguments.length, doUntil); i++) {
            result[i] = arguments[i].cast(position, environment, parameterTypes[i]);
        }

        return result;
    }

    /**
     * Creates the value for the optional Expression
     *
     * @param position    position
     * @param javaMethod  Method to calculate for
     * @param parameterNr which parameter
     * @param environment ZS environment (used for ZS types and Expression creation)
     *
     * @return default optional (0, false, null) or default value according to the @Optional annotation
     */
    private static Expression getOptionalValue(ZenPosition position, JavaMethod javaMethod, int parameterNr, IEnvironmentGlobal environment) {
        Parameter parameter = javaMethod.getMethod().getParameters()[parameterNr];
        Optional optional = parameter.getAnnotation(Optional.class);

        //No annotation (not sure how but lets be sure) -> Default value
        if(optional == null)
            return javaMethod.getParameterTypes()[parameterNr].defaultValue(position);

        Class<?> parameterType = parameter.getType();
        //Primitives
        if(parameterType.isPrimitive()) {
            Class<?> clazz = parameter.getType();
            if(clazz == int.class || clazz == short.class || clazz == long.class || clazz == byte.class)
                return new ExpressionInt(position, optional.valueLong(), environment.getType(clazz));
            else if(clazz == boolean.class)
                return new ExpressionBool(position, optional.valueBoolean());
            else if(clazz == float.class || clazz == double.class)
                return new ExpressionFloat(position, optional.valueDouble(), environment.getType(clazz));
            else {
                //Should never happen
                environment.error(position, "Optional Annotation Error, not a known primitive: " + clazz);
                return new ExpressionInvalid(position);
            }
        }

        Class<?> methodClass = optional.methodClass();
        if(methodClass == Optional.class) {
            //Not a String -> null
            //Empty String -> null;
            //Backwards compat!
            return (parameterType == String.class && !optional.value().isEmpty()) ? new ExpressionString(position, optional.value()) : javaMethod.getParameterTypes()[parameterNr].defaultValue(position);
        }

        try {
            Method method = methodClass.getMethod(optional.methodName(), String.class);
            if(!parameterType.isAssignableFrom(method.getReturnType())) {
                environment.error(position, "Optional Annotation Error, cannot assign " + parameterType + " from " + method);
                return new ExpressionInvalid(position);
            }
            return new ExpressionCallStatic(position, environment, new JavaMethod(method, environment.getEnvironment().getTypeRegistry()), new ExpressionString(position, optional.value()));
        } catch(NoSuchMethodException ignored) {
            //Method not found --> Null
            environment.error(position, "Optional Annotation Error, cannot find method " + optional.methodName());
            return new ExpressionInvalid(position);
        }

    }

    @Override
    public boolean isStatic() {
        return (method.getModifiers() & Modifier.STATIC) > 0;
    }

    @Override
    public boolean isVarargs() {
        return method.isVarArgs();
    }

    @Override
    public ZenType getReturnType() {
        return returnType;
    }

    @Override
    public ZenType[] getParameterTypes() {
        return parameterTypes;
    }

    public Class getOwner() {
        return method.getDeclaringClass();
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
        return getPriority(environment, arguments) > 0;
    }

    @Override
    public boolean accepts(int numArguments) {
        if(numArguments > parameterTypes.length) {
            return method.isVarArgs();
        }
        if(numArguments == parameterTypes.length) {
            return true;
        } else {
            for(int i = numArguments; i < parameterTypes.length; i++) {
                if(!optional[i])
                    return false;
            }
            return true;
        }
    }

    @Override
    public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
        int result = PRIORITY_HIGH;
        if(arguments.length > parameterTypes.length) {
            if(method.isVarArgs()) {
                ZenType arrayType = parameterTypes[method.getParameterTypes().length - 1];
                ZenType baseType = ((ZenTypeArray) arrayType).getBaseType();
                for(int i = parameterTypes.length - 1; i < arguments.length; i++) {
                    ZenType argType = arguments[i].getType();
                    if(argType.equals(baseType)) {
                        // OK
                    } else if(argType.canCastImplicit(baseType, environment)) {
                        result = Math.min(result, PRIORITY_LOW);
                    } else {
                        return PRIORITY_INVALID;
                    }
                }
            } else {
                return PRIORITY_INVALID;
            }
        } else if(arguments.length < parameterTypes.length) {
            result = PRIORITY_MEDIUM;

            int checkUntil = parameterTypes.length;
            for(int i = arguments.length; i < checkUntil; i++) {
                if(!optional[i]) {
                    return PRIORITY_INVALID;
                }
            }
        }

        int checkUntil = arguments.length;
        if(method.isVarArgs())
            checkUntil = parameterTypes.length - 1;
        if(arguments.length == parameterTypes.length && method.isVarArgs()) {
            ZenType arrayType = parameterTypes[method.getParameterTypes().length - 1];
            ZenType baseType = ((ZenTypeArray) arrayType).getBaseType();
            ZenType argType = arguments[arguments.length - 1].getType();

            if(argType.equals(arrayType) || argType.equals(baseType)) {
                // OK
            } else if(argType.canCastImplicit(arrayType, environment)) {
                result = Math.min(result, PRIORITY_LOW);
            } else if(argType.canCastImplicit(baseType, environment)) {
                result = Math.min(result, PRIORITY_LOW);
            } else {
                return PRIORITY_INVALID;
            }

            checkUntil = arguments.length - 1;
        }

        for(int i = 0; i < checkUntil; i++) {
            ZenType argType = arguments[i].getType();
            ZenType paramType = parameterTypes[i];
            if(!argType.equals(paramType)) {
                if(argType.canCastImplicit(paramType, environment)) {
                    result = Math.min(result, PRIORITY_LOW);
                } else {
                    return PRIORITY_INVALID;
                }
            }
        }

        return result;
    }

    @Override
    public void invokeVirtual(MethodOutput output) {
        if(isStatic()) {
            throw new UnsupportedOperationException("Method is static");
        } else {
            if(method.getDeclaringClass().isInterface()) {
                output.invokeInterface(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes());
            } else {
                output.invokeVirtual(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes());
            }
        }
    }

    @Override
    public void invokeStatic(MethodOutput output) {
        if(!isStatic()) {
            throw new UnsupportedOperationException("Method is not static");
        } else {
            output.invokeStatic(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes());
        }
    }

    @Override
    public String toString() {
        return "JavaMethod: " + method.toString();
    }
    
    
    @Override
    public String getErrorDescription() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n").append(method.getName()).append("(");
        for(int i = 0; i < parameterTypes.length; i++) {
            ZenType type = parameterTypes[i];
            for(int i1 = 0; i1 < method.getParameterAnnotations()[i].length; i1++) {
                Annotation an = method.getParameterAnnotations()[i][i1];
                builder.append("\u00a7a").append(an.annotationType().getSimpleName()).append(" ");
            }
            builder.append("\u00a7r").append(type.toString()).append(", ");
        }
    
        //Removes last ', ' and closes the bracket
        final int length = builder.length();
        builder.delete(length - 2, length);
        builder.append(")");
        return builder.toString();
    }
}
