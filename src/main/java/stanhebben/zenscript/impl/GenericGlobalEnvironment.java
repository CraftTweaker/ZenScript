package stanhebben.zenscript.impl;

import stanhebben.zenscript.*;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

import java.lang.reflect.Type;
import java.util.*;

public class GenericGlobalEnvironment implements IEnvironmentGlobal {
    
    private final Map<String, byte[]> classes;
    private final Map<String, IZenSymbol> symbols;
    private final ClassNameGenerator generator;
    private final IZenRegistry registry;
    
    public GenericGlobalEnvironment(Map<String, byte[]> classes, IZenRegistry registry) {
        this.classes = classes;
        symbols = new HashMap<>();
        generator = new ClassNameGenerator();
        this.registry = registry;
    }
    
    @Override
    public IZenCompileEnvironment getEnvironment() {
        return registry.getCompileEnvironment();
    }
    
    @Override
    public TypeExpansion getExpansion(String name) {
        return registry.getExpansions().get(name);
    }
    
    @Override
    public ClassNameGenerator getClassNameGenerator() {
        return generator;
    }
    
    @Override
    public String makeClassName() {
        return generator.generate();
    }
    
    @Override
    public String makeClassNameWithMiddleName(String middleName) {
        return generator.generateWithMiddleName(middleName);
    }
    
    @Override
    public boolean containsClass(String name) {
        return classes.containsKey(name);
    }
    
    @Override
    public Set<String> getClassNames() {
        return classes.keySet();
    }
    
    @Override
    public byte[] getClass(String name) {
        return classes.get(name);
    }
    
    @Override
    public void putClass(String name, byte[] data) {
        classes.put(name, data);
    }
    
    @Override
    public IPartialExpression getValue(String name, ZenPosition position) {
        if(symbols.containsKey(name)) {
            return symbols.get(name).instance(position);
        } else if(registry.getGlobals().containsKey(name)) {
            return registry.getGlobals().get(name).instance(position);
        } else {
            IZenSymbol pkg = registry.getRoot().get(name);
            if(pkg == null) {
                return null;
            } else {
                return pkg.instance(position);
            }
        }
    }
    
    @Override
    public void putValue(String name, IZenSymbol value, ZenPosition position) {
        if(symbols.containsKey(name)) {
            error(position, "Value already defined in this scope: " + name);
        } else {
            symbols.put(name, value);
        }
    }
    
    @Override
    public void error(ZenPosition position, String message) {
        registry.getErrorLogger().error(position, message);
    }
    
    @Override
    public void warning(ZenPosition position, String message) {
        registry.getErrorLogger().warning(position, message);
    }
    
    @Override
    public void info(ZenPosition position, String message) {
        registry.getErrorLogger().info(position, message);
    }
    
    @Override
    public void error(String message) {
        registry.getErrorLogger().error(message);
    }
    
    @Override
    public void error(String message, Throwable e) {
        registry.getErrorLogger().error(message, e);
    }
    
    @Override
    public void warning(String message) {
        registry.getErrorLogger().warning(message);
    }
    
    @Override
    public void info(String message) {
        registry.getErrorLogger().info(message);
    }
    
    @Override
    public ZenType getType(Type type) {
        return registry.getTypes().getType(type);
    }
    
}
