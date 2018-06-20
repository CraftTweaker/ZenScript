package stanhebben.zenscript.type.natives;

import com.google.gson.*;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.dump.types.DumpIJavaMethod;

/**
 * @author Stanneke
 */
public class ZenNativeOperator implements IDumpable {
    
    private final OperatorType operator;
    private final IJavaMethod method;
    
    public ZenNativeOperator(OperatorType operator, IJavaMethod method) {
        this.operator = operator;
        this.method = method;
    }
    
    public OperatorType getOperator() {
        return operator;
    }
    
    public IJavaMethod getMethod() {
        return method;
    }
    
    @Override
    public JsonElement serialize(JsonSerializationContext context) {
        JsonObject obj = new DumpIJavaMethod(method).serialize(context);
        obj.addProperty("operator", operator.toString());
        
        return obj;
    }
}
