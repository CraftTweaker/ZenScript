package stanhebben.zenscript.dump.types;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import stanhebben.zenscript.dump.*;

import java.io.IOException;

public class DumpClassBase implements IDumpedObject {
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
    public void serialize(JsonWriter writer) throws IOException {
        writer.beginObject()
                .name("javaPath").value(getFullPathNameJava())
                .name("zsPath").value(getZsAliasPath());
    }
}
