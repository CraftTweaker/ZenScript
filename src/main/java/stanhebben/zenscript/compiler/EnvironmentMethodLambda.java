package stanhebben.zenscript.compiler;

import stanhebben.zenscript.expression.*;
import stanhebben.zenscript.expression.partial.*;
import stanhebben.zenscript.symbols.*;
import stanhebben.zenscript.util.*;

import java.util.*;

public class EnvironmentMethodLambda extends EnvironmentMethod {
    
    private static final List<Class<? extends IPartialExpression>> nonCapturedExpressions;
    static {
        nonCapturedExpressions = Arrays.asList(PartialStaticGetter.class, PartialStaticGenerated.class, PartialStaticMethod.class, ExpressionJavaStaticField.class, ExpressionJavaMethodStatic.class, ExpressionCallStatic.class);
        
        
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
    
}
