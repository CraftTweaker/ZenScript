package stanhebben.zenscript.compiler;

/**
 * @author Stanneke
 */
public class ClassNameGenerator {
    
    private final String prefix;
    private int counter = 0;
    
    public ClassNameGenerator() {
        this("ZenClass");
    }
    
    public ClassNameGenerator(String prefix) {
        this.prefix = prefix;
    }
    
    public String generate() {
        return this.prefix + counter++;
    }
    
    public String generate(String customPrefix) {
        return customPrefix + counter++;
    }
    
    public String generateWithMiddleName(String customMiddleName) {
        return prefix + customMiddleName + counter++;
    }
}
