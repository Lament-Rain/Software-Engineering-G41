package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.Timer;
import java.util.TimerTask;

public class SearchBarController {
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> searchResults;
    @FXML
    private Button closeButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label noResultsLabel;
    @FXML
    private VBox emptyStateContainer;
    @FXML
    private GridPane hotFeaturesGrid;
    @FXML
    private ListView<String> recentSearches;
    @FXML
    private HBox loadingContainer;

    private Stage stage;
    private List<Feature> features;
    private Consumer<String> onFeatureSelected;
    private static Set<String> searchHistory = new LinkedHashSet<>();
    private Timer searchTimer;
    private static final int DEBOUNCE_DELAY = 300;

    public void setStage(Stage stage) {
        this.stage = stage;
        // 设置舞台居中
        stage.centerOnScreen();
        
        // 添加窗口尺寸变化监听
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            stage.centerOnScreen();
        });
        
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            stage.centerOnScreen();
        });
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
        // 初始化热门功能按钮
        initHotFeatures();
    }

    public void setOnFeatureSelected(Consumer<String> onFeatureSelected) {
        this.onFeatureSelected = onFeatureSelected;
    }

    @FXML
    private void initialize() {
        // 显示空状态
        showEmptyState(true);
        
        // 搜索框文本变化监听
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateClearButtonVisibility(newValue);
            debounceSearch(newValue);
        });

        // 键盘快捷键
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!searchResults.getItems().isEmpty()) {
                    selectFirstResult();
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                closeSearchBar();
            }
        });

        // 结果列表点击事件
        searchResults.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedFeature = searchResults.getSelectionModel().getSelectedItem();
                if (selectedFeature != null) {
                    selectFeature(selectedFeature);
                }
            }
        });

        // 结果列表键盘事件
        searchResults.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String selectedFeature = searchResults.getSelectionModel().getSelectedItem();
                if (selectedFeature != null) {
                    selectFeature(selectedFeature);
                }
            }
        });

        // 清除按钮点击事件
        clearButton.setOnAction(event -> {
            searchField.clear();
            searchField.requestFocus();
        });
        
        // 最近搜索点击事件
        recentSearches.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedSearch = recentSearches.getSelectionModel().getSelectedItem();
                if (selectedSearch != null) {
                    searchField.setText(selectedSearch);
                    searchField.requestFocus();
                }
            }
        });
        
        // 初始化热门功能
        initHotFeatures();
    }

    private void updateClearButtonVisibility(String text) {
        clearButton.setVisible(text != null && !text.isEmpty());
    }

    private void showEmptyState(boolean show) {
        emptyStateContainer.setVisible(show);
        emptyStateContainer.setManaged(show);
        searchResults.setVisible(!show);
        searchResults.setManaged(!show);
        noResultsLabel.setVisible(!show);
        noResultsLabel.setManaged(!show);
        loadingContainer.setVisible(false);
        loadingContainer.setManaged(false);
        
        if (show) {
            updateRecentSearches();
        }
    }

    private void showLoadingState(boolean show) {
        loadingContainer.setVisible(show);
        loadingContainer.setManaged(show);
        searchResults.setVisible(!show);
        searchResults.setManaged(!show);
        noResultsLabel.setVisible(!show);
        noResultsLabel.setManaged(!show);
        emptyStateContainer.setVisible(!show);
        emptyStateContainer.setManaged(!show);
    }

    private void showNoResultsState(boolean show) {
        noResultsLabel.setVisible(show);
        noResultsLabel.setManaged(show);
        searchResults.setVisible(!show);
        searchResults.setManaged(!show);
        loadingContainer.setVisible(false);
        loadingContainer.setManaged(false);
        emptyStateContainer.setVisible(!show);
        emptyStateContainer.setManaged(!show);
    }

    private void showSearchResultsState(boolean show) {
        searchResults.setVisible(show);
        searchResults.setManaged(show);
        searchResults.setPrefHeight(200); // 设置搜索结果列表的高度
        loadingContainer.setVisible(!show);
        loadingContainer.setManaged(!show);
        emptyStateContainer.setVisible(!show);
        emptyStateContainer.setManaged(!show);
        noResultsLabel.setVisible(!show);
        noResultsLabel.setManaged(!show);
    }

    private void debounceSearch(String query) {
        if (searchTimer != null) {
            searchTimer.cancel();
        }
        
        searchTimer = new Timer();
        searchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    searchFeatures(query);
                });
            }
        }, DEBOUNCE_DELAY);
    }

    private void searchFeatures(String query) {
        if (features == null) return;

        if (query == null || query.isEmpty()) {
            showEmptyState(true);
            return;
        }

        showLoadingState(true);

        // 模拟搜索延迟，实际项目中可能是异步搜索
        new Thread(() -> {
            try {
                // 模拟搜索延迟
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<String> matchingFeatures = new ArrayList<>();
            for (Feature feature : features) {
                if (feature.getName().toLowerCase().contains(query.toLowerCase())) {
                    matchingFeatures.add(feature.getName());
                }
            }

            // 在JavaFX应用线程中更新UI
            javafx.application.Platform.runLater(() -> {
                searchResults.setItems(FXCollections.observableArrayList(matchingFeatures));
                if (matchingFeatures.isEmpty()) {
                    showNoResultsState(true);
                } else {
                    // 显示搜索结果
                    showSearchResultsState(true);
                    // 保持搜索框的焦点，让用户可以继续输入
                    searchField.requestFocus();
                    // 将光标移动到搜索框的末尾
                    searchField.positionCaret(searchField.getText().length());
                }
            });
        }).start();
    }

    private void initHotFeatures() {
        // 热门功能列表
        List<String> hotFeatures = new ArrayList<>();
        if (features != null) {
            for (Feature feature : features) {
                hotFeatures.add(feature.getName());
                if (hotFeatures.size() >= 6) break;
            }
        }
        
        // 添加热门功能按钮
        int row = 0;
        int col = 0;
        for (String feature : hotFeatures) {
            Button button = new Button(feature);
            button.getStyleClass().add("search-bar-hot-feature-button");
            button.setOnAction(event -> {
                selectFeature(feature);
            });
            hotFeaturesGrid.add(button, col, row);
            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }

    private void updateRecentSearches() {
        ObservableList<String> recentItems = FXCollections.observableArrayList(searchHistory);
        recentSearches.setItems(recentItems);
    }

    private void addToSearchHistory(String query) {
        if (query != null && !query.isEmpty()) {
            searchHistory.add(query);
            // 限制历史记录数量
            if (searchHistory.size() > 10) {
                // 移除最早的记录
                String first = searchHistory.iterator().next();
                searchHistory.remove(first);
            }
            updateRecentSearches();
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        closeSearchBar();
    }

    private void closeSearchBar() {
        if (stage != null) {
            stage.close();
        }
    }

    private void selectFirstResult() {
        if (!searchResults.getItems().isEmpty()) {
            String firstResult = searchResults.getItems().get(0);
            selectFeature(firstResult);
        }
    }

    private void selectFeature(String featureName) {
        // 先关闭搜索框
        closeSearchBar();
        // 然后执行回调
        if (onFeatureSelected != null) {
            onFeatureSelected.accept(featureName);
        }
        // 添加到搜索历史
        addToSearchHistory(featureName);
    }

    public static class Feature {
        private String name;
        private Runnable action;

        public Feature(String name, Runnable action) {
            this.name = name;
            this.action = action;
        }

        public String getName() {
            return name;
        }

        public Runnable getAction() {
            return action;
        }
    }
}