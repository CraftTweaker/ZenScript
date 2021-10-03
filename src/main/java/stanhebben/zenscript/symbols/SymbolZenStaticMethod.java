package stanhebben.zenscript.symbols;

import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialStaticGenerated;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

import java.util.List;

/**
 * @author Stan
 */
public class SymbolZenStaticMethod implements IZenSymbol {
    
    private final String className;
    private final String methodName;
    private final String signature;
    private final List<ParsedFunctionArgument> arguments;
    private final ZenType returnType;
    
    public SymbolZenStaticMethod(String className, String methodName, String signature, List<ParsedFunctionArgument> arguments, ZenType returnType) {
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.arguments = arguments;
        this.returnType = returnType;
    }
    
    @Override
    public IPartialExpression instance(ZenPosition position) {
        return new PartialStaticGenerated(position, className, methodName, signature, arguments, returnType);
    }
}
