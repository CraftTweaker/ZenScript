package stanhebben.zenscript.dump.types;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import stanhebben.zenscript.dump.IDumpedObject;
import stanhebben.zenscript.type.natives.IJavaMethod;

import java.io.IOException;
import java.lang.reflect.Method;

public class DumpIJavaMethod implements IDumpedObject {
    
    private IJavaMethod method;
    private String methodName;
    
    public DumpIJavaMethod(IJavaMethod method, String methodName) {
        this.method = method;
        this.methodName = methodName;
    }
    
    
    @Override
    public void serialize(JsonWriter out) throws IOException {
        out.beginObject().name("methodName").value(methodName).endObject();
    }
}
