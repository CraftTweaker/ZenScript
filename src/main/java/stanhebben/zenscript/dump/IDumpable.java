package stanhebben.zenscript.dump;

import com.google.gson.*;

public interface IDumpable {
    default JsonElement serialize(JsonSerializationContext context) {return JsonNull.INSTANCE;}
}