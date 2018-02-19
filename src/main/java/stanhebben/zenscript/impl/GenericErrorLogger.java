package stanhebben.zenscript.impl;

import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.util.ZenPosition;

import java.io.*;

public class GenericErrorLogger implements IZenErrorLogger {
    
    private final PrintStream output;
    
    public GenericErrorLogger(PrintStream output) {
        this.output = output;
    }
    
    @Override
    public void error(ZenPosition position, String message) {
        if(position == null) {
            output.println("System> " + message);
        } else {
            output.println(position + "> " + message);
        }
    }
    
    @Override
    public void warning(ZenPosition position, String message) {
        if(position == null) {
            output.println("system> " + message);
        } else {
            output.println(position + "> " + message);
        }
    }
    
    @Override
    public void info(ZenPosition position, String message) {
        if(position == null) {
            output.println("system> " + message);
        } else {
            output.println(position + "> " + message);
        }
    }
    
    @Override
    public void error(String message) {
        error(null, message);
    }
    @Override
    public void error(String message, Throwable e) {
        error(null, message);
        e.printStackTrace(output);
    }
    
    @Override
    public void warning(String message) {
        warning(null, message);
    }
    
    @Override
    public void info(String message) {
        info(null, message);
    }
}
