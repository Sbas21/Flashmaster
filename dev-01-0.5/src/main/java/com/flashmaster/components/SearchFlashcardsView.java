package com.flashmaster.components;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.flashmaster.data.DataAccessLayer;
import com.flashmaster.data.FlashcardFile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SearchFlashcardsView extends VBox {

    private final DataAccessLayer dataAccessLayer;
    private final TextField searchField;
    private final Label resultSummary;
    private final FlashcardsTable flashcardsTable;
    private List<FlashcardFile> allFlashcards;

    public SearchFlashcardsView(DataAccessLayer dataAccessLayer, Runnable onBack) {
        this.dataAccessLayer = dataAccessLayer;
        this.allFlashcards = new ArrayList<>();

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

        Label title = new Label("Search Flashcards");
        title.getStyleClass().add("section-title");

        HBox header = new HBox(12, backButton, title);
        header.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.getStyleClass().add("form-input");
        searchField.setPromptText("Search by deck name, front text, back text, or status");
        searchField.setPrefWidth(420);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("primary-button");
        searchButton.setOnAction(event -> applyCurrentFilter());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> searchField.clear());

        HBox searchControls = new HBox(10, searchField, searchButton, clearButton);
        searchControls.setAlignment(Pos.CENTER_LEFT);

        resultSummary = new Label();
        resultSummary.getStyleClass().add("form-label");

        flashcardsTable = new FlashcardsTable("Flashcard Results");
        VBox.setVgrow(flashcardsTable, Priority.ALWAYS);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyCurrentFilter());
        searchField.setOnAction(event -> applyCurrentFilter());

        reloadFlashcards();
        applyCurrentFilter();

        getChildren().addAll(header, searchControls, resultSummary, flashcardsTable);
    }

    private void reloadFlashcards() {
        if (dataAccessLayer == null) {
            allFlashcards = new ArrayList<>();
            return;
        }
        allFlashcards = new ArrayList<>(dataAccessLayer.getAllFlashcardFiles());
        allFlashcards.sort((a, b) -> {
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
            return dateB.compareTo(dateA);
        });
    }

    private void applyCurrentFilter() {
        String query = normalize(searchField.getText());
        List<FlashcardFile> filtered = new ArrayList<>();
        for (FlashcardFile flashcard : allFlashcards) {
            if (matchesQuery(flashcard, query)) {
                filtered.add(flashcard);
            }
        }

        flashcardsTable.setFlashcards(filtered);
        if (query.isEmpty()) {
            resultSummary.setText("Showing all flashcards: " + filtered.size());
        } else {
            resultSummary.setText("Matches: " + filtered.size());
        }
    }

    private static boolean matchesQuery(FlashcardFile flashcard, String query) {
        if (query.isEmpty()) {
            return true;
        }
        if (flashcard == null) {
            return false;
        }

        return contains(flashcard.getDeckName(), query)
                || contains(flashcard.getFrontText(), query)
                || contains(flashcard.getBackText(), query)
                || contains(flashcard.getStatus(), query);
    }

    private static boolean contains(String value, String query) {
        return normalize(value).contains(query);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
