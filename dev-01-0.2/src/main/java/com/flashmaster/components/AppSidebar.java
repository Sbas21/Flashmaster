package com.flashmaster.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AppSidebar extends VBox {

    // Sidebar items (icon + label)
    private static final String[][] NAV_ITEMS = {
        {"⌂", "Home"},
        {"⚙", "Manage Decks"},
        {"≡", "List Decks"},
    };

    private final List<HBox> navButtons = new ArrayList<>();

    public AppSidebar(Consumer<String> onItemSelected) {
        getStyleClass().add("app-sidebar");
        setSpacing(4);
        setPadding(new Insets(16, 16, 16, 0));
        setPrefWidth(220);
        setMinWidth(200);

        for (int i = 0; i < NAV_ITEMS.length; i++) {
            String icon = NAV_ITEMS[i][0];
            String label = NAV_ITEMS[i][1];
            boolean isActive = (i == 0);

            HBox navButton = createNavButton(icon, label, isActive);
            navButtons.add(navButton);
            
            // Click to switch content
            navButton.setOnMouseClicked(e -> {
                setActiveButton(navButton);
                if (onItemSelected != null) {
                    onItemSelected.accept(label);
                }
            });
            
            getChildren().add(navButton);
        }
    }

    private void setActiveButton(HBox selectedButton) {
        for (HBox btn : navButtons) {
            btn.getStyleClass().remove("sidebar-btn-active");
            // Re-enable hover effect for non-active buttons
            btn.setOnMouseEntered(e -> btn.getStyleClass().add("sidebar-btn-hover"));
            btn.setOnMouseExited(e -> btn.getStyleClass().remove("sidebar-btn-hover"));
        }
        
        selectedButton.getStyleClass().add("sidebar-btn-active");
        selectedButton.getStyleClass().remove("sidebar-btn-hover");
        // Disable hover effect for active button
        selectedButton.setOnMouseEntered(null);
        selectedButton.setOnMouseExited(null);
    }

    public void setActiveByLabel(String labelText) {
        for (HBox btn : navButtons) {
            if (btn.getChildren().size() < 2) {
                continue;
            }
            if (btn.getChildren().get(1) instanceof Label) {
                Label label = (Label) btn.getChildren().get(1);
                if (labelText.equals(label.getText())) {
                    setActiveButton(btn);
                    break;
                }
            }
        }
    }

    private HBox createNavButton(String iconText, String labelText, boolean active) {
        Label icon = new Label(iconText);
        icon.getStyleClass().add("sidebar-icon");

        Label label = new Label(labelText);
        label.getStyleClass().add("sidebar-label");

        HBox button = new HBox(12, icon, label);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(10, 16, 10, 16));
        button.getStyleClass().add("sidebar-btn");

        if (active) {
            button.getStyleClass().add("sidebar-btn-active");
        }

        // Hover effects for non-active items
        if (!active) {
            button.setOnMouseEntered(e -> button.getStyleClass().add("sidebar-btn-hover"));
            button.setOnMouseExited(e -> button.getStyleClass().remove("sidebar-btn-hover"));
        }

        // Cursor hand
        button.setStyle("-fx-cursor: hand;");

        return button;
    }
}
