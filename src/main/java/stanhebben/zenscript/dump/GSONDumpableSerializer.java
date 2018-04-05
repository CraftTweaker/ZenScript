package stanhebben.zenscript.dump;

import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.IOException;
import java.lang.reflect.Type;

public class GSONDumpableSerializer<T extends IDumpedObject> extends TypeAdapter<T> {
    public static final GSONDumpableSerializer INSTANCE = new GSONDumpableSerializer();
    
    /*@Override
    public JsonElement serialize(IDumpedObject src, Type typeOfSrc, JsonSerializationContext context) {
        return src.serialize(context);
    }*/
    
    @Override
    public void write(JsonWriter out, T value) throws IOException {
        value.serialize(out);
        out.flush();
    }
    
    
    @Override
    public T read(JsonReader in) throws IOException {
        throw new IOException("Reading not supported");
    }
}
