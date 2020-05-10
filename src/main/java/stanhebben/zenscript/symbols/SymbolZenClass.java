package stanhebben.zenscript.symbols;

import stanhebben.zenscript.expression.partial.*;
import stanhebben.zenscript.type.*;
import stanhebben.zenscript.util.*;

public class SymbolZenClass implements IZenSymbol {
    
    
    private final ZenTypeZenClass type;
    
    public SymbolZenClass(ZenTypeZenClass type) {
        this.type = type;
    }
    
    @Override
    public IPartialExpression instance(ZenPosition position) {
        return new PartialZSClass(type);
    }
}
