package service;

import javafx.scene.Parent;
import javafx.stage.Stage;
import java.util.Stack;

public class NavigationHistory {
    private static NavigationHistory instance;
    private Stack<NavigationEntry> history;
    
    private NavigationHistory() {
        history = new Stack<>();
    }
    
    public static NavigationHistory getInstance() {
        if (instance == null) {
            instance = new NavigationHistory();
        }
        return instance;
    }
    
    public void addEntry(String pageName, Runnable backAction) {
        history.push(new NavigationEntry(pageName, backAction));
    }
    
    public boolean canGoBack() {
        return history.size() > 1; // At least one entry (current page) remains
    }
    
    public void goBack() {
        if (canGoBack()) {
            history.pop(); // Remove current page
            NavigationEntry previousEntry = history.peek(); // Get previous page
            if (previousEntry != null) {
                previousEntry.getBackAction().run();
            }
        }
    }
    
    public void clear() {
        history.clear();
    }
    
    public int size() {
        return history.size();
    }
    
    private static class NavigationEntry {
        private String pageName;
        private Runnable backAction;
        
        public NavigationEntry(String pageName, Runnable backAction) {
            this.pageName = pageName;
            this.backAction = backAction;
        }
        
        public String getPageName() {
            return pageName;
        }
        
        public Runnable getBackAction() {
            return backAction;
        }
    }
}
