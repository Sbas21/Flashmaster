package com.flashmaster.components;

import com.flashmaster.data.DataAccessLayer;
import com.flashmaster.data.DeckFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ReviewDecksView extends VBox {

    private final DataAccessLayer dataAccessLayer;
    private final Consumer<DeckFile> onDeckSelected;
    private final TextField searchField;
    private final Label resultSummary;
    private final DecksTable decksTable;
    private List<DeckFile> allDecks;

    public ReviewDecksView(DataAccessLayer dataAccessLayer, Runnable onBack, Consumer<DeckFile> onDeckSelected) {
        this.dataAccessLayer = dataAccessLayer;
        this.onDeckSelected = onDeckSelected;
        this.allDecks = new ArrayList<>();

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

        Label title = new Label("Review");
        title.getStyleClass().add("section-title");

        HBox header = new HBox(12, backButton, title);
        header.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.getStyleClass().add("form-input");
        searchField.setPromptText("Search deck name or description");
        searchField.setPrefWidth(420);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("primary-button");
        searchButton.setOnAction(event -> applyCurrentFilter());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> {
            searchField.clear();
            applyCurrentFilter();
        });

        HBox searchControls = new HBox(10, searchField, searchButton, clearButton);
        searchControls.setAlignment(Pos.CENTER_LEFT);

        resultSummary = new Label();
        resultSummary.getStyleClass().add("form-label");

        decksTable = new DecksTable("Decks");
        decksTable.setOnDeckClicked(deck -> {
            if (deck != null && this.onDeckSelected != null) {
                this.onDeckSelected.accept(deck);
            }
        });
        VBox.setVgrow(decksTable, Priority.ALWAYS);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyCurrentFilter());
        searchField.setOnAction(event -> applyCurrentFilter());

        reloadDecks();
        applyCurrentFilter();

        getChildren().addAll(header, searchControls, resultSummary, decksTable);
    }

    private void reloadDecks() {
        allDecks = new ArrayList<>();
        if (dataAccessLayer == null) {
            return;
        }
        for (DeckFile deck : dataAccessLayer.getAllDeckFiles()) {
            if (deck != null) {
                allDecks.add(deck);
            }
        }
    }

    private void applyCurrentFilter() {
        String query = normalize(searchField.getText());
        List<DeckFile> filtered = new ArrayList<>();
        for (DeckFile deck : allDecks) {
            if (deck == null) {
                continue;
            }
            if (query.isEmpty() || contains(deck.getDeckName(), query) || contains(deck.getDescription(), query)) {
                filtered.add(deck);
            }
        }

        decksTable.setDecks(filtered);
        if (query.isEmpty()) {
            resultSummary.setText("Showing all decks: " + filtered.size());
        } else {
            resultSummary.setText("Matches: " + filtered.size());
        }
    }

    private static boolean contains(String value, String query) {
        return normalize(value).contains(query);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
