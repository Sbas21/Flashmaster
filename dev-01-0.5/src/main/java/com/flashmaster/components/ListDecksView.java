package com.flashmaster.components;

import com.flashmaster.data.DataAccessLayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ListDecksView extends VBox {
    public ListDecksView(DataAccessLayer dataAccessLayer, Runnable onBack) {
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

        Label title = new Label("List Decks");
        title.getStyleClass().add("section-title");

        HBox header = new HBox(12, backButton, title);
        header.setAlignment(Pos.CENTER_LEFT);

        // Table of stored decks
        DecksTable decksTable = new DecksTable("All Decks");
        VBox.setVgrow(decksTable, Priority.ALWAYS);
        if (dataAccessLayer != null) {
            // Load from CSV
            decksTable.setDecks(dataAccessLayer.getAllDeckFiles());
        }

        getChildren().addAll(header, decksTable);
    }
}
