package com.flashmaster.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ManageFlashcardsView extends VBox {
    public ManageFlashcardsView(Runnable onBack, Runnable onDefineFlashcard) {
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

        Label title = new Label("Manage Flashcards");
        title.getStyleClass().add("section-title");

        Button defineFlashcardButton = new Button("Define Flashcard");
        defineFlashcardButton.getStyleClass().add("primary-button");
        defineFlashcardButton.setOnAction(event -> {
            if (onDefineFlashcard != null) {
                onDefineFlashcard.run();
            }
        });

        HBox header = new HBox(12, backButton, title, defineFlashcardButton);
        header.setAlignment(Pos.CENTER_LEFT);

        getChildren().add(header);
    }
}
