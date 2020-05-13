package stanhebben.zenscript.compiler;

import org.objectweb.asm.*;
import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.expression.partial.*;
import stanhebben.zenscript.symbols.*;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.*;

import java.util.*;

public class EnvironmentMethodLambda extends EnvironmentMethod {
    
    private static final List<Class<? extends IPartialExpression>> nonCapturedExpressions;
    static {
        nonCapturedExpressions = new ArrayList<>();
        nonCapturedExpressions.add(PartialGlobalValue.class);
        nonCapturedExpressions.add(PartialJavaClass.class);
        nonCapturedExpressions.add(PartialPackage.class);
        nonCapturedExpressions.add(PartialScriptReference.class);
        nonCapturedExpressions.add(PartialStaticGenerated.class);
        nonCapturedExpressions.add(PartialStaticGetter.class);
        nonCapturedExpressions.add(PartialStaticMethod.class);
        nonCapturedExpressions.add(PartialType.class);
        nonCapturedExpressions.add(PartialZSClass.class);
    }
    
    private final List<SymbolCaptured> capturedVariables;
    private final String clsName;
    
    public EnvironmentMethodLambda(MethodOutput output, IEnvironmentClass environment, String clsName) {
        super(output, environment);
        this.clsName = clsName;
        capturedVariables = new ArrayList<>(0);
    }
    
    @Override
    public IPartialExpression getValue(String name, ZenPosition position) {
        if(local.containsKey(name)) {
            return local.get(name).instance(position);
        } else {
            final IPartialExpression value = environment.getValue(name, position);
            if(value != null) {
                if(nonCapturedExpressions.stream().anyMatch(c -> c.isInstance(value))) {
                    return value;
                }
                
                
                final SymbolCaptured capture = new SymbolCaptured(value.eval(environment), name, clsName);
                capturedVariables.add(capture);
                local.put(name, capture);
                return capture.instance(position);
            }
            return null;
        }
    }
    
    public List<SymbolCaptured> getCapturedVariables() {
        return capturedVariables;
    }
    
    
    public void createConstructor(ClassWriter cw) {
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
    }
}
