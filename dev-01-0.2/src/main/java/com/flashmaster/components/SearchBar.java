package com.flashmaster.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Search bar footer with text input and search button.
 * Mirrors the React SearchBar component.
 */
public class SearchBar extends HBox {

    public SearchBar() {
        getStyleClass().add("search-bar");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);
        setPadding(new Insets(16, 32, 16, 32));

        // --- Search Input ---
        TextField searchField = new TextField();
        searchField.setPromptText("Search Decks or Flashcards...");
        searchField.getStyleClass().add("search-input");
        searchField.setPrefWidth(350);
        searchField.setMaxWidth(400);

        // --- Search Button ---
        Button searchButton = new Button("\uD83D\uDD0D  Search");
        searchButton.getStyleClass().add("search-button");

        getChildren().addAll(searchField, searchButton);
    }
}
