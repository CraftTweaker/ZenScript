package stanhebben.zenscript.dump.types;

import com.google.gson.*;
import stanhebben.zenscript.type.natives.*;

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
    }

    @SuppressWarnings("Duplicates")
    @Override
    public JsonObject serialize(JsonSerializationContext context) {
        JsonObject obj = super.serialize(context);
        
        JsonObject memberMap = new JsonObject();
        JsonArray castersArray = new JsonArray();
        
        members.forEach((name, zenNativeMember) -> zenTypeNativeMemberHelper(memberMap, name,  zenNativeMember, false, context));
        staticMembers.forEach((name, zenNativeMember) -> zenTypeNativeMemberHelper(memberMap, name,  zenNativeMember, true, context));
        obj.add("members", memberMap);
        
        if (!casters.isEmpty()) {
            for(ZenNativeCaster caster : casters) {
                castersArray.add(caster.asDumpedObject().get(0).serialize(context));
            }
    
            obj.add("casters", castersArray);
        }
    
        for(ZenNativeOperator trinaryOperator : trinaryOperators) {
        
        }
        
    
        return obj;
    }
    
    /**
     *
     * @param memberMap Map to add the entries to
     * @param name ZS name of the member
     * @param zenNativeMember the actual member
     */
    private void zenTypeNativeMemberHelper(JsonObject memberMap, String name, ZenNativeMember zenNativeMember, boolean isStatic, JsonSerializationContext context){
        JsonObject jsonMember = new JsonObject();
        
        if(zenNativeMember.getGetter() != null)
            jsonMember.add("getter", new DumpIJavaMethod(zenNativeMember.getGetter()).withStaticOverride(isStatic).serialize(context));
    
        if(zenNativeMember.getSetter() != null)
            jsonMember.add("setter", new DumpIJavaMethod(zenNativeMember.getSetter()).withStaticOverride(isStatic).serialize(context));
        
        
        List<IJavaMethod> methodList = zenNativeMember.getMethods();
        if (!methodList.isEmpty()) {
            JsonArray methodArray = new JsonArray();
        
            for(IJavaMethod iJavaMethod : zenNativeMember.getMethods()) {
                methodArray.add(new DumpIJavaMethod(iJavaMethod).serialize(context));
            }
        }
        
        memberMap.add(name, jsonMember);
    }
}
