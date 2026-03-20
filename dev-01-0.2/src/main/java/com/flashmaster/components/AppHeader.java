package com.flashmaster.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Application header with gradient title bar and horizontal menu bar.
 * Mirrors the React AppHeader component.
 */
public class AppHeader extends VBox {

    private static final String[] MENU_ITEMS = {"File", "Decks", "Flashcards", "Reports", "Help"};

    public AppHeader() {
        getStyleClass().add("app-header");

        // --- Gradient Title Banner ---
        Label titleLabel = new Label("FlashMaster");
        titleLabel.getStyleClass().add("header-title");

        HBox titleBar = new HBox(titleLabel);
        titleBar.getStyleClass().add("header-gradient");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(20, 32, 20, 32));

        // --- Menu Bar ---
        HBox menuBar = new HBox(24);
        menuBar.getStyleClass().add("header-menu-bar");
        menuBar.setAlignment(Pos.CENTER_LEFT);
        menuBar.setPadding(new Insets(8, 32, 8, 32));

        for (String item : MENU_ITEMS) {
            Label menuItem = new Label(item);
            menuItem.getStyleClass().add("menu-item");

            // Hover effect
            menuItem.setOnMouseEntered(e -> menuItem.getStyleClass().add("menu-item-hover"));
            menuItem.setOnMouseExited(e -> menuItem.getStyleClass().remove("menu-item-hover"));

            menuBar.getChildren().add(menuItem);
        }

        getChildren().addAll(titleBar, menuBar);
    }
}
