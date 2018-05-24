package stanhebben.zenscript.dump.types;

import com.google.gson.*;
import stanhebben.zenscript.dump.*;

public class DumpClassBase implements IDumpable {
    private String classNameJava;
    private String fullPathNameJava;
    private String zsAliasPath;
    private String zsAliasClassName;
    
    public DumpClassBase(String fullPathName, String zsAliasPath) {
        this.fullPathNameJava = fullPathName;
        this.zsAliasPath = zsAliasPath;
        this.zsAliasClassName = DumpUtils.getClassNameFromPath(zsAliasPath);
        this.classNameJava = DumpUtils.getClassNameFromPath(fullPathNameJava);
    }
    
    public String getClassNameJava() {
        return classNameJava;
    }
    
    public String getFullPathNameJava() {
        return fullPathNameJava;
    }
    
    public String getZsAliasPath() {
        return zsAliasPath;
    }
    
    public String getZsAliasClassName() {
        return zsAliasClassName;
    }
    
    @Override
    public JsonObject serialize(JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("javaPath", getFullPathNameJava());
        obj.addProperty("zsPath", getZsAliasPath());
        
        return obj;
    }
    
    @Override
    public String toString() {
        return "DumpClassBase: {" + fullPathNameJava + "}";
    }
}
