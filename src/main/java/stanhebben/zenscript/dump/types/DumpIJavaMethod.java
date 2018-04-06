package stanhebben.zenscript.dump.types;

import com.google.gson.*;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.type.natives.*;

public class DumpIJavaMethod implements IDumpable {
    
    private transient IJavaMethod method;
    private boolean staticOverride = false;
    
    public DumpIJavaMethod(IJavaMethod method) {
        this.method = method;
    }
    
    @Override
    public JsonObject serialize(JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        
        obj.addProperty("static", method.isStatic() || staticOverride);
        obj.addProperty("returnClass", method.getReturnType().toJavaClass().getCanonicalName());
        obj.addProperty("name", method.toString());
        
        if (method instanceof JavaMethodGenerated) {
            obj.addProperty("isSynthetic", true);
        }
        
        return obj;
    }
    
    public DumpIJavaMethod withStaticOverride(boolean override){
        staticOverride = override;
        return this;
    }
}
