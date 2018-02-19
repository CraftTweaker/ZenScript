package stanhebben.zenscript.impl;

import stanhebben.zenscript.IZenLogger;

public class GenericLogger implements IZenLogger {
    
    @Override
    public void error(String message) {
        System.out.println("[ERROR] " + message);
    }
    
    @Override
    public void error(String message, Throwable e) {
        System.out.println("[ERROR] " + message);
        e.printStackTrace();
    }
    
    @Override
    public void warning(String message) {
        System.out.println("[WARNING] " + message);
    }
    
    @Override
    public void info(String message) {
        System.out.println("[INFO] " + message);
    }
}
