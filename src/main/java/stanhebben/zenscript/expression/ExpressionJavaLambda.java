package stanhebben.zenscript.expression;

import org.objectweb.asm.*;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.symbols.*;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.*;

import java.lang.reflect.*;
import java.util.*;

import static stanhebben.zenscript.util.ZenTypeUtil.*;

/**
 * @author Stanneke
 */
public class ExpressionJavaLambda extends Expression {
    
    private final Class<?> interfaceClass;
    private final List<ParsedFunctionArgument> arguments;
    private final List<Statement> statements;
    
    private final ZenType type;
    
    public ExpressionJavaLambda(ZenPosition position, Class<?> interfaceClass, List<ParsedFunctionArgument> arguments, List<Statement> statements, ZenType type) {
        super(position);
        
        this.interfaceClass = interfaceClass;
        this.arguments = arguments;
        this.statements = statements;
        
        this.type = type;
    }
    
    @Override
    public ZenType getType() {
        return type;
    }
    
    @Override
    public void compile(boolean result, IEnvironmentMethod environment) {
        if(!result)
            return;
        
        Method method = interfaceClass.getMethods()[0];
        
        // generate class
        String clsName = environment.makeClassNameWithMiddleName(getPosition().getFile().getClassName());
        
        ClassWriter cw = new ZenClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, clsName, null, "java/lang/Object", new String[]{internal(interfaceClass)});
        
        MethodOutput output = new MethodOutput(cw, Opcodes.ACC_PUBLIC, method.getName(), descriptor(method), null, null);
        
        IEnvironmentClass environmentClass = new EnvironmentClass(cw, environment);
        EnvironmentMethodLambda environmentMethod = new EnvironmentMethodLambda(output, environmentClass, clsName);
        
        for(int i = 0, j = 0; i < arguments.size(); i++) {
            environmentMethod.putValue(arguments.get(i).getName(), new SymbolArgument(i + j + 1, environment.getType(method.getGenericParameterTypes()[i])), getPosition());
            if(environment.getType(method.getGenericParameterTypes()[i]).isLarge())
                j++;
        }
        
        output.start();
        for(Statement statement : statements) {
            statement.compile(environmentMethod);
        }
        output.ret();
        output.end();
    
    
        final List<SymbolCaptured> capturedVariables = environmentMethod.getCapturedVariables();
        final StringJoiner sj = new StringJoiner("", "(", ")V");
        for(SymbolCaptured value : capturedVariables) {
            cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, value.getFieldName(), Type.getDescriptor(value.getType().toJavaClass()), null, null).visitEnd();
            sj.add(Type.getDescriptor(value.getType().toJavaClass()));
        }
    
        MethodOutput constructor = new MethodOutput(cw, Opcodes.ACC_PUBLIC, "<init>", sj.toString(), null, null);
        constructor.start();
        constructor.loadObject(0);
        constructor.invokeSpecial("java/lang/Object", "<init>", "()V");
    
        {
            int i = 1, j = 0;
            for(SymbolCaptured capturedVariable : capturedVariables) {
                final ZenType type = capturedVariable.getType();
                constructor.loadObject(0);
                constructor.load(Type.getType(type.toJavaClass()), i + j);
                if(type.isLarge()) {
                    j++;
                }
                constructor.putField(clsName, capturedVariable.getFieldName(), Type.getDescriptor(capturedVariable.getType().toJavaClass()));
                i++;
            }
        }
        
        constructor.ret();
        constructor.end();
        
        
        environment.putClass(clsName, cw.toByteArray());
        
        // make class instance
        environment.getOutput().newObject(clsName);
        environment.getOutput().dup();
        final String[] arguments = capturedVariables.stream()
                .map(SymbolCaptured::getEvaluated)
                .peek(expression -> expression.compile(true, environment))
                .map(Expression::getType)
                .map(ZenType::toASMType)
                .map(Type::getDescriptor)
                .toArray(String[]::new);
        environment.getOutput().construct(clsName, arguments);
    }
}
