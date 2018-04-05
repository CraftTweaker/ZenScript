package stanhebben.zenscript.dump.types;

import com.google.gson.*;
import stanhebben.zenscript.dump.IDumpedObject;

public class DumpDummy implements IDumpedObject {
    private String className;
    private String toString;
    
    public DumpDummy(Object self) {
        this.className = self.getClass().getCanonicalName();
        this.toString = self.toString();
    }
    
    @Override
    public JsonElement serialize(JsonSerializationContext context) {
        return new JsonPrimitive(className +  " : {" + toString + "}");
    }
}
