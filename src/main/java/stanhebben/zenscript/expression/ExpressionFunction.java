package stanhebben.zenscript.expression;

import org.objectweb.asm.*;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.symbols.*;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.*;
import stanhebben.zenscript.util.localvariabletable.LocalVariable;
import stanhebben.zenscript.util.localvariabletable.LocalVariableTable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Stanneke
 */
public class ExpressionFunction extends Expression {
    
    private final List<ParsedFunctionArgument> arguments;
    private final ZenType returnType;
    private final List<Statement> statements;
    
    private final ZenTypeFunctionCallable functionType;
    private final String className;
    
    public ExpressionFunction(ZenPosition position, List<ParsedFunctionArgument> arguments, ZenType returnType, List<Statement> statements, String className) {
        super(position);
        
        System.out.println("Function expression: " + arguments.size() + " arguments");
        
        this.arguments = arguments;
        this.returnType = returnType;
        this.statements = statements;
        
        ZenType[] argumentTypes = new ZenType[arguments.size()];
        for(int i = 0; i < arguments.size(); i++) {
            argumentTypes[i] = arguments.get(i).getType();
        }
        
        this.className = className;
        functionType = new ZenTypeFunctionCallable(returnType, argumentTypes, className, makeDescriptor());
    }
    
    @Override
    public Expression cast(ZenPosition position, IEnvironmentGlobal environment, ZenType type) {
        if(type instanceof ZenTypeNative) {
            ZenTypeNative nativeType = (ZenTypeNative) type;
            Class<?> nativeClass = nativeType.getNativeClass();
            final Method method = ZenTypeUtil.findFunctionalInterfaceMethod(nativeClass);
            if(method != null) {
                // functional interface
                if(returnType != ZenTypeAny.INSTANCE && !returnType.canCastImplicit(environment.getType(method.getGenericReturnType()), environment)) {
                    environment.error(position, "return type not compatible");
                    return new ExpressionInvalid(position);
                }
                if(arguments.size() != method.getParameterTypes().length) {
                    environment.error(getPosition(), String.format("Expected %s arguments, received %s arguments", method.getParameterTypes().length, arguments.size()));
                    return new ExpressionInvalid(position);
                }
                for(int i = 0; i < arguments.size(); i++) {
                    ZenType argumentType = environment.getType(method.getGenericParameterTypes()[i]);
                    if(arguments.get(i).getType() != ZenTypeAny.INSTANCE && !argumentType.canCastImplicit(arguments.get(i).getType(), environment)) {
                        environment.error(position, "argument " + i + " doesn't match");
                        return new ExpressionInvalid(position);
                    }
                }
                
                return new ExpressionJavaLambda(position, nativeClass, arguments, statements, environment.getType(nativeClass));
            } else {
                environment.error(position, type.toString() + " is not a functional interface");
                return new ExpressionInvalid(position);
            }
        } else if(type instanceof ZenTypeFunction) {
            boolean matches = returnType.equals(((ZenTypeFunction) type).getReturnType());
            ZenType[] args = ((ZenTypeFunction) type).getArgumentTypes();
            if(matches) {
                for(int i = 0; i < arguments.size(); i++) {
                    matches &= arguments.get(i).getType().equals(args[i]);
                }
            }
            if(matches)
                return this;
            environment.error(position, "Cannot cast a function literal to " + type.toString());
            return new ExpressionInvalid(position);
            
        } else {
            environment.error(position, "Cannot cast a function literal to " + type.toString());
            return new ExpressionInvalid(position);
        }
    }
    
    @Override
    public ZenType getType() {
        return functionType;
    }
    
    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if(!result)
            return;
        
        functionType.writeInterfaceClass(environment);
        
        ClassWriter cw = new ZenClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", new String[]{functionType.getInterfaceName()});
        cw.visitSource(getPosition().getFileName(), null);
        
        MethodOutput output = new MethodOutput(cw, Opcodes.ACC_PUBLIC, "accept", functionType.getDescriptor(), null, null);
        
        IEnvironmentClass environmentClass = new EnvironmentClass(cw, environment);
        EnvironmentMethodLambda environmentMethod = new EnvironmentMethodLambda(output, environmentClass, className);
        LocalVariableTable localVariableTable = environmentMethod.getLocalVariableTable();
        localVariableTable.beginScope();
        for(int i = 0, j = 0; i < arguments.size(); i++) {
            SymbolArgument symbolArgument = new SymbolArgument(i + 1 + j, arguments.get(i).getType());
            environmentMethod.putValue(arguments.get(i).getName(), symbolArgument, getPosition());
            localVariableTable.put(LocalVariable.parameter(arguments.get(i).getName(), symbolArgument));
            if(arguments.get(i).getType().isLarge())
                j++;
        }
        
        output.start();
        for(Statement statement : statements) {
            statement.compile(environmentMethod);
        }
        localVariableTable.ensureFirstLabel(output, getPosition());
        output.ret();
        localVariableTable.endMethod(output);
        localVariableTable.writeLocalVariables(output);
        output.end();
        
        environmentMethod.createConstructor(cw);
        environment.putClass(className, cw.toByteArray());
        
        // make class instance
        environment.getOutput().newObject(className);
        environment.getOutput().dup();
        final String[] arguments = environmentMethod.getCapturedVariables().stream().map(SymbolCaptured::getEvaluated).peek(expression -> expression.compile(true, environment)).map(Expression::getType).map(ZenType::toASMType).map(Type::getDescriptor).toArray(String[]::new);
        environment.getOutput().construct(className, arguments);
    }
    
    private String makeDescriptor() {
        StringBuilder sb = new StringBuilder("(");
        arguments.stream().map(ParsedFunctionArgument::getType).map(ZenType::getSignature).forEach(sb::append);
        sb.append(")").append(returnType.getSignature());
        return sb.toString();
    }
    
    public String getClassName() {
        return className;
    }
    
    public String getDescriptor() {
        return functionType.getDescriptor();
    }
}
