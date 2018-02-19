package stanhebben.zenscript.impl;

import stanhebben.zenscript.*;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.compiler.*;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.*;
import stanhebben.zenscript.type.ZenTypeNative;
import stanhebben.zenscript.type.natives.*;
import stanhebben.zenscript.util.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.logging.*;

public class GenericRegistry implements IZenRegistry {
    
    private IZenCompileEnvironment compileEnvironment;
    
    private Map<String, IZenSymbol> globals = new HashMap<>();
    private Set<Pair<Integer, IBracketHandler>> bracketHandlers = new TreeSet<>(Comparator.comparingInt((ToIntFunction<Pair<Integer, IBracketHandler>>) Pair::getKey).thenComparing(o -> o.getValue().getClass().getName()));
    private TypeRegistry types = new TypeRegistry();
    private SymbolPackage root = new SymbolPackage("<root>");
    private Map<String, TypeExpansion> expansions = new HashMap<>();
    private IZenErrorLogger errorLogger;
    private IZenLogger logger = new GenericLogger();
    
    public GenericRegistry(IZenCompileEnvironment compileEnvironment, IZenErrorLogger errorLogger) {
        this.compileEnvironment = compileEnvironment;
        this.errorLogger = errorLogger;
        this.compileEnvironment.setRegistry(this);
    }
    
    public void registerGlobal(String name, IZenSymbol symbol) {
        if(globals.containsKey(name)) {
            throw new IllegalArgumentException("symbol already exists: " + name);
        }
        
        globals.put(name, symbol);
    }
    
    public void registerExpansion(Class<?> cls) {
        try {
            for(Annotation annotation : cls.getAnnotations()) {
                if(annotation instanceof ZenExpansion) {
                    ZenExpansion eAnnotation = (ZenExpansion) annotation;
                    if(!expansions.containsKey(eAnnotation.value())) {
                        expansions.put(eAnnotation.value(), new TypeExpansion(eAnnotation.value()));
                    }
                    expansions.get(eAnnotation.value()).expand(cls, types);
                }
            }
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    public void registerBracketHandler(IBracketHandler handler) {
        int prio = 10;
        if(handler.getClass().getAnnotation(BracketHandler.class) != null) {
            prio = handler.getClass().getAnnotation(BracketHandler.class).priority();
        } else {
            getLogger().info(handler.getClass().getName() + " is missing a BracketHandler annotation, setting the priority to " + prio);
        }
        bracketHandlers.add(new Pair<>(prio, handler));
    }
    
    public void removeBracketHandler(IBracketHandler handler) {
        Pair<Integer, IBracketHandler> prioPair = null;
        for(Pair<Integer, IBracketHandler> pair : bracketHandlers) {
            if(pair.getValue().equals(handler)) {
                prioPair = pair;
            }
        }
        bracketHandlers.remove(prioPair);
    }
    
    public void registerNativeClass(Class<?> cls) {
        try {
            ZenTypeNative type = new ZenTypeNative(cls);
            type.complete(types);
            root.put(type.getName(), new SymbolType(type), errorLogger);
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    public IZenSymbol getStaticFunction(Class cls, String name, Class... arguments) {
        IJavaMethod method = JavaMethod.get(types, cls, name, arguments);
        return new SymbolJavaStaticMethod(method);
    }
    
    public IZenSymbol getStaticField(Class cls, String name) {
        try {
            Field field = cls.getDeclaredField(name);
            return new SymbolJavaStaticField(cls, field, types);
        } catch(NoSuchFieldException | SecurityException ex) {
            getLogger().error("Unable to get static field: " + name + " from class " + cls.getName(), ex);
            return null;
        }
    }
    
    public IZenSymbol resolveBracket(IEnvironmentGlobal environment, List<Token> tokens) {
        for(Pair<Integer, IBracketHandler> pair : bracketHandlers) {
            IZenSymbol symbol = pair.getValue().resolve(environment, tokens);
            if(symbol != null) {
                return symbol;
            }
        }
        
        return null;
    }
    
    public IEnvironmentGlobal makeGlobalEnvironment(Map<String, byte[]> classes) {
        return new GenericGlobalEnvironment(classes, this);
    }
    
    public IZenCompileEnvironment getCompileEnvironment() {
        return compileEnvironment;
    }
    
    public Map<String, IZenSymbol> getGlobals() {
        return globals;
    }
    
    public Set<Pair<Integer, IBracketHandler>> getBracketHandlers() {
        return bracketHandlers;
    }
    
    public TypeRegistry getTypes() {
        return types;
    }
    
    public SymbolPackage getRoot() {
        return root;
    }
    
    public Map<String, TypeExpansion> getExpansions() {
        return expansions;
    }
    
    public void setCompileEnvironment(IZenCompileEnvironment compileEnvironment) {
        this.compileEnvironment = compileEnvironment;
    }
    
    public void setGlobals(Map<String, IZenSymbol> globals) {
        this.globals = globals;
    }
    
    public void setBracketHandlers(Set<Pair<Integer, IBracketHandler>> bracketHandlers) {
        this.bracketHandlers = bracketHandlers;
    }
    
    public void setTypes(TypeRegistry types) {
        this.types = types;
    }
    
    public void setRoot(SymbolPackage root) {
        this.root = root;
    }
    
    public void setExpansions(Map<String, TypeExpansion> expansions) {
        this.expansions = expansions;
    }
    
    public IZenErrorLogger getErrorLogger() {
        return errorLogger;
    }
    
    public void setErrorLogger(IZenErrorLogger errorLogger) {
        this.errorLogger = errorLogger;
    }
    
    public IZenLogger getLogger() {
        return logger;
    }
    
    public void setLogger(IZenLogger logger) {
        this.logger = logger;
    }
}
