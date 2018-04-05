package stanhebben.zenscript.dump;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public interface IDumpedObject {
    void serialize(JsonWriter out) throws IOException;
}