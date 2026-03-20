package com.flashmaster.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Placeholder component for the Manage Decks view.
 */
public class ManageDecksView extends VBox {
    public ManageDecksView(Runnable onBack, Runnable onDefineDeck) {
        getStyleClass().add("content-page");
        setAlignment(Pos.TOP_LEFT);
        setSpacing(16);
        setPadding(new Insets(24));

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        Label title = new Label("Manage Decks");
        title.getStyleClass().add("section-title");

        Button defineDeckButton = new Button("Define Deck");
        defineDeckButton.getStyleClass().add("primary-button");
        defineDeckButton.setOnAction(event -> {
            if (onDefineDeck != null) {
                onDefineDeck.run();
            }
        });

        HBox header = new HBox(12, backButton, title, defineDeckButton);
        header.setAlignment(Pos.CENTER_LEFT);

        getChildren().add(header);
    }
}
