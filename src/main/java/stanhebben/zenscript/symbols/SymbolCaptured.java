package stanhebben.zenscript.symbols;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.expression.partial.*;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.*;

import java.util.*;

public class SymbolCaptured implements IZenSymbol {
    
    private final String fieldName;
    private final String lambdaClassName;
    private final Expression evaluated;
    
    
    public SymbolCaptured(Expression original, String fieldName, String clsName) {
        this.evaluated = original;
        this.fieldName = fieldName;
        this.lambdaClassName = clsName;
    }
    
    public ZenType getType() {
        return evaluated.getType();
    }
    
    @Override
    public IPartialExpression instance(ZenPosition position) {
        return new Expression(position) {
            @Override
            public void compile(boolean result, IEnvironmentMethod environment) {
                if(!result)
                    return;
                
                final MethodOutput output = environment.getOutput();
                if(lambdaClassName == null || fieldName == null || evaluated == null) {
                    throw new IllegalStateException(String.format(Locale.ENGLISH, "Captured variable with name %s in class %s and evaluated obj %s has at least one null info", fieldName, lambdaClassName, evaluated));
                }
                
                
                output.loadObject(0);
                output.getField(lambdaClassName, fieldName, getType().toASMType().getDescriptor());
            }
            
            @Override
            public ZenType getType() {
                return SymbolCaptured.this.getType();
            }
        };
    }
    
    public Expression getEvaluated() {
        return evaluated;
    }
    
    public String getFieldName() {
        return fieldName;
    }
}
