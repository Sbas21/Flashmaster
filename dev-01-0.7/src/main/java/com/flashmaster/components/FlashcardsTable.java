package com.flashmaster.components;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    private Consumer<FlashcardFile> onFlashcardSelected;

    private Consumer<FlashcardFile> onFlashcardDoubleClicked;


    public FlashcardsTable(String titleText) {
        getStyleClass().add("decks-table-container");
        setSpacing(16);
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);

        Label title = new Label(titleText == null ? "All Flashcards" : titleText);
        title.getStyleClass().add("section-title");

        table = new TableView<>();
        table.getStyleClass().add("decks-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setMinWidth(0);
        table.setMaxWidth(Double.MAX_VALUE);
        table.setPlaceholder(new Label("No flashcards found."));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (onFlashcardSelected != null) {
                onFlashcardSelected.accept(newSelection);
            }
        });

        table.setRowFactory(tv -> {
            javafx.scene.control.TableRow<FlashcardFile> row = new javafx.scene.control.TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    FlashcardFile clickedFlashcard = row.getItem();

                    if (onFlashcardDoubleClicked != null) {
                        onFlashcardDoubleClicked.accept(clickedFlashcard);
                    }
                }
            });

            return row;
        });

        TableColumn<FlashcardFile, String> deckNameCol = new TableColumn<>("Deck Name");
        deckNameCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            String value = flashcard == null ? "" : flashcard.getDeckName();
            return new SimpleStringProperty(value == null ? "" : value);
        });
        deckNameCol.setMinWidth(90);
        deckNameCol.setPrefWidth(130);

        TableColumn<FlashcardFile, String> frontTextCol = new TableColumn<>("Front Text");
        frontTextCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            String value = flashcard == null ? "" : firstLine(flashcard.getFrontText());
            return new SimpleStringProperty(value == null ? "" : value);
        });
        frontTextCol.setMinWidth(120);
        frontTextCol.setPrefWidth(220);

        TableColumn<FlashcardFile, String> backTextCol = new TableColumn<>("Back Text");
        backTextCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            String value = flashcard == null ? "" : firstLine(flashcard.getBackText());
            return new SimpleStringProperty(value == null ? "" : value);
        });
        backTextCol.setMinWidth(120);
        backTextCol.setPrefWidth(220);

        TableColumn<FlashcardFile, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            String value = flashcard == null ? "" : flashcard.getStatus();
            return new SimpleStringProperty(value == null ? "" : value);
        });
        statusCol.setMinWidth(90);
        statusCol.setPrefWidth(120);

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
        creationDateTextCol.setMinWidth(110);
        creationDateTextCol.setPrefWidth(140);

        TableColumn<FlashcardFile, String> lastReviewDateCol = new TableColumn<>("Last Review Date");
        lastReviewDateCol.setCellValueFactory(cell -> {
            FlashcardFile flashcard = cell.getValue();
            LocalDate value = flashcard == null ? null : flashcard.getLastReviewDate();
            return new SimpleStringProperty(value == null ? "" : value.toString());
        });
        lastReviewDateCol.setMinWidth(110);
        lastReviewDateCol.setPrefWidth(140);

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

    public void setOnFlashcardSelected(Consumer<FlashcardFile> onFlashcardSelected) {
        this.onFlashcardSelected = onFlashcardSelected;
    }

    public void setOnFlashcardDoubleClicked(Consumer<FlashcardFile> handler) {
        this.onFlashcardDoubleClicked = handler;
    }

    public FlashcardFile getSelectedFlashcard() {
        return table.getSelectionModel().getSelectedItem();
    }
    
    public void clearSelection() {
        table.getSelectionModel().clearSelection();
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

    private static String firstLine(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String normalized = value.replace("\r\n", "\n").replace('\r', '\n');
        int firstNewlineIndex = normalized.indexOf('\n');
        if (firstNewlineIndex < 0) {
            return normalized;
        }
        return normalized.substring(0, firstNewlineIndex);
    }
}
