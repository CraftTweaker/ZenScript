package stanhebben.zenscript.expression;

import org.objectweb.asm.*;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.symbols.*;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.*;

import java.lang.reflect.Method;
import java.util.*;

import static stanhebben.zenscript.util.ZenTypeUtil.*;

/**
 * @author Stanneke
 */
public class ExpressionJavaLambdaSimpleGeneric extends Expression {

    private final Class interfaceClass;
    public Class genericClass;
    private final List<ParsedFunctionArgument> arguments;
    private final List<Statement> statements;
    private final String descriptor;

    private final ZenType type;

    public ExpressionJavaLambdaSimpleGeneric(ZenPosition position, Class<?> interfaceClass, List<ParsedFunctionArgument> arguments, List<Statement> statements, ZenType type) {
        super(position);

        this.interfaceClass = interfaceClass;
        this.arguments = arguments;
        this.statements = statements;

        this.type = type;

        ZenType genericType = arguments.get(0).getType();
        this.genericClass = genericType.equals(ZenType.ANY) ? Object.class : genericType.toJavaClass();
    
        final Method method = ZenTypeUtil.findFunctionalInterfaceMethod(interfaceClass);
        if(method == null) {
            throw new IllegalArgumentException("Internal error: Cannot create function for " + interfaceClass + " because it is not a functional interface!");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(int i = 0; i < arguments.size(); i++) {
            ZenType t = arguments.get(i).getType();
            if(t.equals(ZenType.ANY)) {
                sb.append(signature(method.getParameterTypes()[i]));
            } else {
                sb.append(t.getSignature());
            }
        }
        sb.append(")").append(signature(interfaceClass.getDeclaredMethods()[0].getReturnType()));
        this.descriptor = sb.toString();
    }

    @Override
    public ZenType getType() {
        return type;
    }

    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if(!result)
            return;

        final Method method = ZenTypeUtil.findFunctionalInterfaceMethod(interfaceClass);
        if(method == null) {
            //How did we even come this far?
            environment.error("Internal error: Cannot create function for " + interfaceClass + " because it is not a functional interface!");
            return;
        }
        // generate class
        String clsName = environment.makeClassNameWithMiddleName(getPosition().getFile().getClassName());

        ClassWriter cw = new ZenClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visitSource(getPosition().getFileName(), null);
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, clsName, createMethodSignature(), "java/lang/Object", new String[]{internal(interfaceClass)});

        MethodOutput output = new MethodOutput(cw, Opcodes.ACC_PUBLIC, method.getName(), descriptor, null, null);
        output.position(getPosition());
        IEnvironmentClass environmentClass = new EnvironmentClass(cw, environment);
        EnvironmentMethodLambda environmentMethod = new EnvironmentMethodLambda(output, environmentClass, clsName);

        for(int i = 0, j = 0; i < arguments.size(); i++) {
            ZenType typeToPut = arguments.get(i).getType();
            if(typeToPut.equals(ZenType.ANY))
                typeToPut = environment.getType(method.getGenericParameterTypes()[i]);
            if(typeToPut == null)
                typeToPut = environment.getType(method.getParameterTypes()[i]);

            environmentMethod.putValue(arguments.get(i).getName(), new SymbolArgument(i + 1 + j, typeToPut), getPosition());
            if(typeToPut.isLarge())
                j++;
        }

        output.start();
        for(Statement statement : statements) {
            statement.compile(environmentMethod);
        }
        output.ret();
        output.end();

        if(!Objects.equals(genericClass, Object.class)) {
            MethodOutput bridge = new MethodOutput(cw, Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE, method.getName(), ZenTypeUtil.descriptor(method), null, null);
            bridge.loadObject(0);
            bridge.loadObject(1);
            bridge.checkCast(internal(genericClass));
            if(arguments.size() > 1) {
                for(int i = 1; i < arguments.size(); ) {
                    bridge.load(org.objectweb.asm.Type.getType(method.getParameterTypes()[i]), ++i);
                }
            }

            bridge.invokeVirtual(clsName, method.getName(), descriptor);
            bridge.returnType(org.objectweb.asm.Type.getReturnType(method));
            bridge.end();
        }

        environmentMethod.createConstructor(cw);
        environment.putClass(clsName, cw.toByteArray());
    
        // make class instance
        environment.getOutput().newObject(clsName);
        environment.getOutput().dup();
        final String[] arguments = environmentMethod.getCapturedVariables().stream()
                .map(SymbolCaptured::getEvaluated)
                .peek(expression -> expression.compile(true, environment))
                .map(Expression::getType)
                .map(ZenType::toASMType)
                .map(Type::getDescriptor)
                .toArray(String[]::new);
        environment.getOutput().construct(clsName, arguments);
    }

    private String createMethodSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ljava/lang/Object;");
        sb.append(signature(interfaceClass));
        sb.deleteCharAt(sb.length() - 1);
        sb.append("<").append(signature(genericClass)).append(">").append(";");
        return sb.toString();
    }
}
