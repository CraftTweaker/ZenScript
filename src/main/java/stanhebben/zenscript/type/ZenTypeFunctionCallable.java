package stanhebben.zenscript.type;

import org.objectweb.asm.*;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.definitions.*;
import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.type.casting.*;
import stanhebben.zenscript.type.natives.*;
import stanhebben.zenscript.util.*;

import java.util.*;

/**
 * @author Stanneke
 */
public class ZenTypeFunctionCallable extends ZenTypeFunction {
    
    private final String className;
    private final String descriptor;
    private final String interfaceName;
    private final Map<ZenType, CastingRuleMatchedFunction> implementedInterfaces = new HashMap<>();
    
    public ZenTypeFunctionCallable(ZenType returnType, List<ParsedFunctionArgument> arguments, String className, String descriptor) {
        super(returnType, arguments);
        this.className = className;
        this.descriptor = descriptor;
        this.interfaceName = makeInterfaceName(returnType, argumentTypes);
    }
    
    public ZenTypeFunctionCallable(ZenType returnType, ZenType[] argumentTypes, String className, String descriptor) {
        super(returnType, argumentTypes);
        this.className = className;
        this.descriptor = descriptor;
        this.interfaceName = makeInterfaceName(returnType, argumentTypes);
    }
    
    public ZenTypeFunctionCallable(ZenType returnType, ZenType[] argumentTypes, String className) {
        super(returnType, argumentTypes);
        this.className = className;
        StringBuilder sb = new StringBuilder("(");
        Arrays.stream(argumentTypes).map(ZenType::getSignature).forEach(sb::append);
        sb.append(")").append(returnType.getSignature());
        this.descriptor = sb.toString();
        this.interfaceName = makeInterfaceName(returnType, argumentTypes);
    }
    
    private static String makeInterfaceName(ZenType returnType, ZenType[] argumentTypes) {
        final StringJoiner stringJoiner = new StringJoiner("_");
        for(ZenType argumentType : argumentTypes) {
            stringJoiner.add(argumentType.getName());
        }
        stringJoiner.add("to");
        stringJoiner.add(returnType.getName());
        stringJoiner.add("generated_interface");
        return stringJoiner.toString();
    }
    
    public String getClassName() {
        return className;
    }
    
    @Override
    public String getSignature() {
        return "L" + getInterfaceName() + ";";
    }
    
    @Override
    public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
        return new ExpressionFunctionCall(position, arguments, returnType, receiver, interfaceName, descriptor);
    }
    
    @Override
    public ZenType[] predictCallTypes(int numArguments) {
        return Arrays.copyOf(argumentTypes, numArguments);
    }
    
    @Override
    public Class toJavaClass() {
        // TODO: complete
        return null;
    }
    
    @Override
    public Type toASMType() {
        return Type.getType(getSignature());
    }
    
    public String getInterfaceName() {
        return interfaceName;
    }
    
    @Override
    public Expression defaultValue(ZenPosition position) {
        return new ExpressionNull(position);
    }
    
    public void writeInterfaceClass(IEnvironmentMethod environment) {
        if(environment.containsClass(interfaceName)) {
            return;
        }
    
        ClassWriter cw = new ZenClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SYNTHETIC, interfaceName, null, "java/lang/Object", new String[0]);
        cw.visitSource("generated_interface", null);
        final MethodVisitor accept = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT, "accept", descriptor, null, null);
        accept.visitEnd();
        cw.visitEnd();
        environment.putClass(interfaceName, cw.toByteArray());
    }
    
    public String getDescriptor() {
        return descriptor;
    }
    
    public IJavaMethod createMethod(String fieldName) {
        return new JavaMethodGenerated(false, true, false, interfaceName, "accept", returnType, argumentTypes, new boolean[argumentTypes.length]) {
            @Override
            public void invokeVirtual(MethodOutput output) {
                super.invokeVirtual(output);
            }
        };
    }
}
