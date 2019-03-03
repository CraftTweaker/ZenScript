package stanhebben.zenscript;

import org.junit.jupiter.api.Assertions;

import java.util.function.IntFunction;

public class TestAssertions {
    
    public static void assertMany(String... lines) {
        if(lines == null || lines.length == 0)
            throw new IllegalArgumentException("Calling assertMany with a null or empty vararg is useless!");
        for(int i = 0; i < lines.length; i++) {
            Assertions.assertEquals(lines[i], TestHelper.prints.get(i));
        }
    }
    
    public static void assertOne(String content, int logLine) {
        Assertions.assertEquals(content, TestHelper.prints.get(logLine));
    }
    
    public static void assertCalculated(IntFunction<String> function) {
        assertCalculated(function, TestHelper.prints.size());
    }
    
    public static void assertCalculated(IntFunction<String> function, int count) {
        assertCalculated(function, 0, count);
    }
    
    public static void assertCalculated(IntFunction<String> function, int startIndexLog, int count) {
        assertCalculated(function, startIndexLog, count, 0);
    }
    
    public static void assertCalculated(IntFunction<String> function, int startIndexLog, int count, int startIndexFunction) {
        for(int i = 0; i < count; i++) {
            Assertions.assertEquals(function.apply(i + startIndexFunction), TestHelper.prints.get(i + startIndexLog));
        }
    }
}
