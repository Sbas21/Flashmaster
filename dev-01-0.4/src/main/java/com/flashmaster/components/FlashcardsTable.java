package com.flashmaster.components;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.flashmaster.data.FlashcardFile;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class FlashcardsTable extends VBox {

    private final TableView<FlashcardFile> table;
    private final TableColumn<FlashcardFile, LocalDate> creationDateCol;

    public FlashcardsTable(String titleText) {
        getStyleClass().add("decks-table-container");
        setSpacing(16);

        Label title = new Label(titleText == null ? "All Flashcards" : titleText);
        title.getStyleClass().add("section-title");

        table = new TableView<>();
        table.getStyleClass().add("decks-table");
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No flashcards found."));

        TableColumn<FlashcardFile, String> deckNameCol = new TableColumn<>("Deck Name");
        deckNameCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            String value = flashcard == null ? "" : flashcard.getDeckName();
            return new SimpleStringProperty(value == null ? "" : value);
        });
        deckNameCol.setMinWidth(140);

        TableColumn<FlashcardFile, String> frontTextCol = new TableColumn<>("Front Text");
        frontTextCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            String value = flashcard == null ? "" : flashcard.getFrontText();
            return new SimpleStringProperty(value == null ? "" : value);
        });
        frontTextCol.setMinWidth(220);

        TableColumn<FlashcardFile, String> backTextCol = new TableColumn<>("Back Text");
        backTextCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            String value = flashcard == null ? "" : flashcard.getBackText();
            return new SimpleStringProperty(value == null ? "" : value);
        });
        backTextCol.setMinWidth(220);

        TableColumn<FlashcardFile, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            String value = flashcard == null ? "" : flashcard.getStatus();
            return new SimpleStringProperty(value == null ? "" : value);
        });
        statusCol.setMinWidth(120);

        creationDateCol = new TableColumn<>("Creation Date");
        creationDateCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            LocalDate value = flashcard == null ? null : flashcard.getCreationDate();
            return new javafx.beans.property.SimpleObjectProperty<>(value);
        });
        creationDateCol.setMinWidth(140);
        creationDateCol.setComparator((a, b) -> {
            if (a == null && b == null) {
                return 0;
            }
            if (a == null) {
                return -1;
            }
            if (b == null) {
                return 1;
            }
            return a.compareTo(b);
        });

        TableColumn<FlashcardFile, String> creationDateTextCol = new TableColumn<>("Created On");
        creationDateTextCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            LocalDate value = flashcard == null ? null : flashcard.getCreationDate();
            return new SimpleStringProperty(value == null ? "" : value.toString());
        });
        creationDateTextCol.setMinWidth(140);

        TableColumn<FlashcardFile, String> lastReviewDateCol = new TableColumn<>("Last Review Date");
        lastReviewDateCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            LocalDate value = flashcard == null ? null : flashcard.getLastReviewDate();
            return new SimpleStringProperty(value == null ? "" : value.toString());
        });
        lastReviewDateCol.setMinWidth(140);

        table.getColumns().addAll(
                deckNameCol,
                frontTextCol,
                backTextCol,
                statusCol,
                creationDateTextCol,
                lastReviewDateCol
        );

        table.setPrefHeight(240);
        table.setMinHeight(160);

        VBox.setVgrow(this, Priority.ALWAYS);
        getChildren().addAll(title, table);
    }

    public void setFlashcards(List<FlashcardFile> flashcards) {
        List<FlashcardFile> sorted = new ArrayList<>();
        if (flashcards != null) {
            sorted.addAll(flashcards);
        }

        sorted.sort((a, b) -> {
            LocalDate dateA = a == null ? null : a.getCreationDate();
            LocalDate dateB = b == null ? null : b.getCreationDate();

            if (dateA == null && dateB == null) {
                return 0;
            }
            if (dateA == null) {
                return 1;
            }
            if (dateB == null) {
                return -1;
            }
            return dateB.compareTo(dateA); // descending: newest first
        });

        ObservableList<FlashcardFile> data = FXCollections.observableArrayList(sorted);
        table.setItems(data);
    }
}