package stanhebben.zenscript.dump;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class CustomTypeAdapterFactory implements com.google.gson.TypeAdapterFactory {
    
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (IDumpedObject.class.isAssignableFrom(type.getRawType())) return null;
        
        return GSONDumpableSerializer.INSTANCE;
    }
}
