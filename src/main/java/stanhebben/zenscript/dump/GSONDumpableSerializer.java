package stanhebben.zenscript.dump;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GSONDumpableSerializer implements JsonSerializer<IDumpedObject> {
    public static final GSONDumpableSerializer INSTANCE = new GSONDumpableSerializer();
    
    @Override
    public JsonElement serialize(IDumpedObject src, Type typeOfSrc, JsonSerializationContext context) {
        return src.serialize(context);
    }
}
