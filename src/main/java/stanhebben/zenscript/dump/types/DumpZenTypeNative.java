package stanhebben.zenscript.dump.types;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import stanhebben.zenscript.type.natives.*;

import java.io.IOException;
import java.util.*;

public class DumpZenTypeNative extends DumpZenType {
    
    private final Map<String, ZenNativeMember> members;
    private final Map<String, ZenNativeMember> staticMembers;
    private final List<ZenNativeCaster> casters;
    private final List<ZenNativeOperator> trinaryOperators;
    private final List<ZenNativeOperator> binaryOperators;
    private final List<ZenNativeOperator> unaryOperators;
    
    public DumpZenTypeNative(Class<?> clazz, String zsAliasPath, Map<String, ZenNativeMember> members, Map<String, ZenNativeMember> staticMembers, List<ZenNativeCaster> casters, List<ZenNativeOperator> trinaryOperators, List<ZenNativeOperator> binaryOperators, List<ZenNativeOperator> unaryOperators) {
        super(clazz, zsAliasPath);
        
        // private stuff from inside of ZenTypeNative
        this.members = members;
        this.staticMembers = staticMembers;
        this.casters = casters;
        this.trinaryOperators = trinaryOperators;
        this.binaryOperators = binaryOperators;
        this.unaryOperators = unaryOperators;
    
        // goes over all non static members
        
    
    
    }
    
    @Override
    public void serialize(JsonWriter writer) throws IOException {
        super.serialize(writer);
        
        writer.name("members").beginArray();
    
        for(Map.Entry<String, ZenNativeMember> entry : members.entrySet()) {
            writer.name(entry.getKey()).beginArray();
    
            for(IJavaMethod iJavaMethod : entry.getValue().getMethods()) {
                writer.value("\t" + iJavaMethod.toString());
            }
    
            if(entry.getValue().getGetter() != null) {
                writer.value("Getter: " + entry.getValue().getGetter().toString());
            }
            if(entry.getValue().getSetter() != null) {
                writer.value("Setter: " + entry.getValue().getSetter().toString());
            }
            
            writer.endArray();
        }
        writer.endArray();
        
        writer.name("staticMembers").beginArray();
    
        for(Map.Entry<String, ZenNativeMember> entry : members.entrySet()) {
            writer.name(entry.getKey()).beginArray();
    
            for(IJavaMethod iJavaMethod : entry.getValue().getMethods()) {
                writer.value("\t" + iJavaMethod.toString());
            }
    
            if(entry.getValue().getGetter() != null) {
                writer.value("Getter: " + entry.getValue().getGetter().toString());
            }
            if(entry.getValue().getSetter() != null) {
                writer.value("Setter: " + entry.getValue().getSetter().toString());
            }
            
            writer.endArray();
        }
        writer.endArray();
    
    
        staticMembers.forEach((s, zenNativeMember) -> {
            staticMembersArray.add("Static Members: " + s);
            for(IJavaMethod iJavaMethod : zenNativeMember.getMethods()) {
                staticMembersArray.add("\t" + iJavaMethod.toString());
            }
        
            if(zenNativeMember.getGetter() != null) {
                staticMembersArray.add("Static Getter: " + zenNativeMember.getGetter().toString());
            }
            if(zenNativeMember.getSetter() != null) {
                staticMembersArray.add("Static Setter: " + zenNativeMember.getSetter().toString());
            }
        });
    
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public JsonObject serialize(JsonSerializationContext context) {
        JsonObject obj = super.serialize(context);
    
    
        JsonArray membersArray = new JsonArray();
        JsonArray staticMembersArray = new JsonArray();
        JsonArray castersArray = new JsonArray();
        
        
        staticMembers.forEach((s, zenNativeMember) -> {
            staticMembersArray.add("Static Members: " + s);
            for(IJavaMethod iJavaMethod : zenNativeMember.getMethods()) {
                staticMembersArray.add("\t" + iJavaMethod.toString());
            }
        
            if(zenNativeMember.getGetter() != null) {
                staticMembersArray.add("Static Getter: " + zenNativeMember.getGetter().toString());
            }
            if(zenNativeMember.getSetter() != null) {
                staticMembersArray.add("Static Setter: " + zenNativeMember.getSetter().toString());
            }
        });
    
        for(ZenNativeCaster caster : casters) {
            castersArray.add(caster.asDumpedObject().get(0).serialize(context));
        }
        
        obj.add("members", membersArray);
        obj.add("staticMembers", staticMembersArray);
        
    
        return obj;
    }
}
