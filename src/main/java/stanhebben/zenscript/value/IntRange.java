package stanhebben.zenscript.value;

import java.util.*;

/**
 * @author Stan Hebben
 */
public class IntRange {

    private final int from;
    private final int to;
    private final Random rand;

    public IntRange(int from, int to) {
        this.from = from;
        this.to = to;
        rand = new Random(2906);
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
    
    public int getMin() {
        return getFrom();
    }
    
    public int getMax() {
        return getTo();
    }
    
    public int getRandom() {
        return rand.nextInt((to - from) + 1) + from;
    }
}
