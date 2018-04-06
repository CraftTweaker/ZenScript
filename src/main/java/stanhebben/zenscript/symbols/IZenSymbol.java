package stanhebben.zenscript.symbols;

import stanhebben.zenscript.dump.*;
import stanhebben.zenscript.dump.types.DumpDummy;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.util.ZenPosition;

import java.util.*;

/**
 * @author Stanneke
 */
public interface IZenSymbol extends IDumpConvertable {
    
    IPartialExpression instance(ZenPosition position);
    
    @Override
    default List<? extends IDumpable> asDumpedObject() {
        return Collections.singletonList(new DumpDummy(this));
    }
}
