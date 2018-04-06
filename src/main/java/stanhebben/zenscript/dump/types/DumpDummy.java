package stanhebben.zenscript.dump.types;

import com.google.gson.*;
import stanhebben.zenscript.dump.IDumpable;

public class DumpDummy implements IDumpable {
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
