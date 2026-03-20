package com.flashmaster.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Define Deck page for creating a new deck.
 */
public class DefineDeckView extends VBox {
    public DefineDeckView(Runnable onBack) {
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

        Label title = new Label("Define Deck");
        title.getStyleClass().add("section-title");

        HBox header = new HBox(12, backButton, title);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Deck Name *");
        nameLabel.getStyleClass().add("form-label");

        TextField nameField = new TextField();
        nameField.getStyleClass().add("form-input");
        nameField.setPromptText("e.g., CS151 - Design Patterns");

        Label nameError = new Label();
        nameError.getStyleClass().add("form-error");
        nameError.setVisible(false);
        nameError.setManaged(false);

        Label descriptionLabel = new Label("Description");
        descriptionLabel.getStyleClass().add("form-label");

        TextArea descriptionArea = new TextArea();
        descriptionArea.getStyleClass().add("form-textarea");
        descriptionArea.setPromptText("Optional description");
        descriptionArea.setPrefRowCount(4);

        Button createButton = new Button("Create Deck");
        createButton.getStyleClass().add("primary-button");
        createButton.setOnAction(event -> {
            String deckName = nameField.getText() == null ? "" : nameField.getText().trim();
            if (deckName.isEmpty()) {
                nameError.setText("Deck Name is required.");
                nameError.setVisible(true);
                nameError.setManaged(true);
                if (!nameField.getStyleClass().contains("input-error")) {
                    nameField.getStyleClass().add("input-error");
                }
                return;
            }

            nameError.setVisible(false);
            nameError.setManaged(false);
            nameField.getStyleClass().remove("input-error");
        });

        nameField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                nameError.setVisible(false);
                nameError.setManaged(false);
                nameField.getStyleClass().remove("input-error");
            }
        });

        VBox form = new VBox(12, nameLabel, nameField, nameError, descriptionLabel, descriptionArea, createButton);
        form.setAlignment(Pos.TOP_LEFT);

        getChildren().addAll(header, form);
    }
}
