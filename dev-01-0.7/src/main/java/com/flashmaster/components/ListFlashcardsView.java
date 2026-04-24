package com.flashmaster.components;

import java.util.Comparator;
import java.util.List;

import com.flashmaster.data.DataAccessLayer;
import com.flashmaster.data.FlashcardFile;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ListFlashcardsView extends VBox {
    public ListFlashcardsView(DataAccessLayer dataAccessLayer, Runnable onBack) {
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

        Label title = new Label("List Flashcards");
        title.getStyleClass().add("section-title");

        HBox header = new HBox(12, backButton, title);
        header.setAlignment(Pos.CENTER_LEFT);

        FlashcardsTable flashcardsTable = new FlashcardsTable("All Flashcards");
        VBox.setVgrow(flashcardsTable, Priority.ALWAYS);

        if (dataAccessLayer != null) {
            List<FlashcardFile> flashcards = dataAccessLayer.getAllFlashcardFiles();

            flashcards.sort(
                    Comparator.comparing(FlashcardFile::getCreationDate).reversed()
            );

            flashcardsTable.setFlashcards(FXCollections.observableArrayList(flashcards));
        }

        getChildren().addAll(header, flashcardsTable);
    }
}