package stanhebben.zenscript.symbols;

import stanhebben.zenscript.dump.IDumpedObject;
import stanhebben.zenscript.expression.partial.*;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

import java.util.*;

/**
 * @author Stan
 */
public class SymbolType implements IZenSymbol {
    
    private final ZenType type;
    
    public SymbolType(ZenType type) {
        this.type = type;
    }
    
    @Override
    public IPartialExpression instance(ZenPosition position) {
        return new PartialType(position, type);
    }
    
    @Override
    public String toString() {
        return "SymbolType: " + type.toString();
    }
    
    public ZenType getType() {
        return type;
    }
    
    @Override
    public List<? extends IDumpedObject> asDumpedObject() {
        return type.asDumpedObject();
    }
}
