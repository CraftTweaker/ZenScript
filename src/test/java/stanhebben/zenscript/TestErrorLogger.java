package stanhebben.zenscript;

import stanhebben.zenscript.util.ZenPosition;

import java.util.ArrayList;
import java.util.List;

public class TestErrorLogger implements IZenErrorLogger {
    
    final List<String> listInfo;
    final List<String> listWarning;
    final List<String> listError;
    
    TestErrorLogger() {
        listInfo = new ArrayList<>();
        listError = new ArrayList<>();
        listWarning = new ArrayList<>();
    }
    
    @Override
    public void error(ZenPosition position, String message) {
        listError.add(position + ": " + message);
    }
    
    @Override
    public void warning(ZenPosition position, String message) {
        listWarning.add(position + ": " + message);
    }
    
    @Override
    public void info(ZenPosition position, String message) {
        listInfo.add(position + ": " + message);
    }
    
    @Override
    public void error(String message) {
        listError.add(message);
    }
    
    @Override
    public void error(String message, Throwable e) {
        listError.add(message);
    }
    
    @Override
    public void warning(String message) {
        listWarning.add(message);
    }
    
    @Override
    public void info(String message) {
        listInfo.add(message);
    }
    
    void clear() {
        this.listInfo.clear();
        this.listWarning.clear();
        this.listError.clear();
    }
}
