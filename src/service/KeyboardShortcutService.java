package service;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class KeyboardShortcutService {

    public interface ShortcutHandler {
        void handle();
    }

    private final Map<String, ShortcutHandler> shortcuts;
    private Stage stage;

    public KeyboardShortcutService(Stage stage) {
        this.stage = stage;
        this.shortcuts = new HashMap<>();
        if (stage != null) {
            setupGlobalShortcuts();
        }
    }

    private void setupGlobalShortcuts() {
        if (stage == null) return;
        
        // Add a listener to detect when scene changes
        stage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    private final KeyCodeCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
                    private final KeyCodeCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
                    private final KeyCodeCombination ctrlN = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
                    private final KeyCodeCombination ctrlE = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
                    private final KeyCodeCombination ctrlR = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
                    private final KeyCodeCombination esc = new KeyCodeCombination(KeyCode.ESCAPE);

                    @Override
                    public void handle(KeyEvent event) {
                        if (ctrlS.match(event)) {
                            event.consume();
                            ShortcutHandler handler = shortcuts.get("ctrl+s");
                            if (handler != null) handler.handle();
                        } else if (ctrlF.match(event)) {
                            event.consume();
                            ShortcutHandler handler = shortcuts.get("ctrl+f");
                            if (handler != null) handler.handle();
                        } else if (ctrlN.match(event)) {
                            event.consume();
                            ShortcutHandler handler = shortcuts.get("ctrl+n");
                            if (handler != null) handler.handle();
                        } else if (ctrlE.match(event)) {
                            event.consume();
                            ShortcutHandler handler = shortcuts.get("ctrl+e");
                            if (handler != null) handler.handle();
                        } else if (ctrlR.match(event)) {
                            event.consume();
                            ShortcutHandler handler = shortcuts.get("ctrl+r");
                            if (handler != null) handler.handle();
                        } else if (esc.match(event)) {
                            event.consume();
                            ShortcutHandler handler = shortcuts.get("escape");
                            if (handler != null) handler.handle();
                        }
                    }
                });
            }
        });
        
        // Also setup for current scene if it exists
        if (stage.getScene() != null) {
            Scene currentScene = stage.getScene();
            currentScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                private final KeyCodeCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
                private final KeyCodeCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
                private final KeyCodeCombination ctrlN = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
                private final KeyCodeCombination ctrlE = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
                private final KeyCodeCombination ctrlR = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
                private final KeyCodeCombination esc = new KeyCodeCombination(KeyCode.ESCAPE);

                @Override
                public void handle(KeyEvent event) {
                    if (ctrlS.match(event)) {
                        event.consume();
                        ShortcutHandler handler = shortcuts.get("ctrl+s");
                        if (handler != null) handler.handle();
                    } else if (ctrlF.match(event)) {
                        event.consume();
                        ShortcutHandler handler = shortcuts.get("ctrl+f");
                        if (handler != null) handler.handle();
                    } else if (ctrlN.match(event)) {
                        event.consume();
                        ShortcutHandler handler = shortcuts.get("ctrl+n");
                        if (handler != null) handler.handle();
                    } else if (ctrlE.match(event)) {
                        event.consume();
                        ShortcutHandler handler = shortcuts.get("ctrl+e");
                        if (handler != null) handler.handle();
                    } else if (ctrlR.match(event)) {
                        event.consume();
                        ShortcutHandler handler = shortcuts.get("ctrl+r");
                        if (handler != null) handler.handle();
                    } else if (esc.match(event)) {
                        event.consume();
                        ShortcutHandler handler = shortcuts.get("escape");
                        if (handler != null) handler.handle();
                    }
                }
            });
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        if (stage != null) {
            setupGlobalShortcuts();
        }
    }

    public void registerShortcut(String shortcutId, ShortcutHandler handler) {
        shortcuts.put(shortcutId.toLowerCase(), handler);
    }

    public void unregisterShortcut(String shortcutId) {
        shortcuts.remove(shortcutId.toLowerCase());
    }

    public void clearAllShortcuts() {
        shortcuts.clear();
    }

    public void setupTabNavigation(Parent root) {
        if (root != null) {
            root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.TAB) {
                        Scene currentScene = stage.getScene();
                        if (currentScene != null) {
                            Node focusedNode = currentScene.getFocusOwner();
                            if (focusedNode != null) {
                                Node nextNode = getNextNode(root, focusedNode, !event.isShiftDown());
                                if (nextNode != null) {
                                    event.consume();
                                    nextNode.requestFocus();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private Node getNextNode(Parent parent, Node current, boolean forward) {
        return parent.lookupAll("*").stream()
            .filter(node -> node instanceof Node)
            .filter(node -> node.isFocusTraversable())
            .filter(node -> {
                if (node instanceof javafx.scene.control.TextInputControl) {
                    return ((javafx.scene.control.TextInputControl) node).isEditable();
                }
                return true;
            })
            .filter(node -> {
                if (node instanceof javafx.scene.control.TextInputControl) {
                    return true;
                }
                if (node instanceof javafx.scene.control.ComboBox) {
                    return true;
                }
                if (node instanceof javafx.scene.control.Button) {
                    return true;
                }
                if (node instanceof javafx.scene.control.CheckBox) {
                    return true;
                }
                if (node instanceof javafx.scene.control.RadioButton) {
                    return true;
                }
                return false;
            })
            .filter(node -> {
                javafx.geometry.Bounds bounds = node.getLayoutBounds();
                return bounds.getWidth() > 0 && bounds.getHeight() > 0;
            })
            .findFirst()
            .orElse(null);
    }

    public void setupEnterKeyNavigation(Parent root) {
        if (root != null) {
            root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.ENTER) {
                        Scene currentScene = stage.getScene();
                        if (currentScene != null) {
                            Node focusedNode = currentScene.getFocusOwner();
                            if (focusedNode instanceof javafx.scene.control.TextField) {
                                event.consume();
                                ShortcutHandler handler = shortcuts.get("enter");
                                if (handler != null) {
                                    handler.handle();
                                } else {
                                    Node nextNode = getNextNode(root, focusedNode, true);
                                    if (nextNode != null) {
                                        nextNode.requestFocus();
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public void disableFocusFor(Node... nodes) {
        for (Node node : nodes) {
            node.setFocusTraversable(false);
        }
    }

    public void enableFocusFor(Node... nodes) {
        for (Node node : nodes) {
            node.setFocusTraversable(true);
        }
    }

    public static String getShortcutText(String shortcutId) {
        switch (shortcutId.toLowerCase()) {
            case "ctrl+s": return "Ctrl+S";
            case "ctrl+f": return "Ctrl+F";
            case "ctrl+n": return "Ctrl+N";
            case "ctrl+e": return "Ctrl+E";
            case "ctrl+r": return "Ctrl+R";
            case "escape": return "Esc";
            case "enter": return "Enter";
            case "tab": return "Tab";
            default: return shortcutId;
        }
    }
}
