package stanhebben.zenscript.dump;

import com.google.gson.*;

public interface IDumpedObject {
    JsonElement serialize(JsonSerializationContext context);
}