package com.flashmaster.components;

import com.flashmaster.data.DeckFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Decks table showing stored deck data.
 */
public class DecksTable extends VBox {

    private final TableView<DeckFile> table;
    private final TableColumn<DeckFile, String> nameCol;

    public DecksTable(String titleText) {
        getStyleClass().add("decks-table-container");
        setSpacing(16);

        // Title label
        Label title = new Label(titleText == null ? "All Decks" : titleText);
        title.getStyleClass().add("section-title");

        // Main table
        table = new TableView<>();
        table.getStyleClass().add("decks-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No decks found."));

        nameCol = new TableColumn<>("Deck Name");
        nameCol.setCellValueFactory(cell -> {
            DeckFile deck = cell.getValue();
            String value = deck == null ? "" : deck.getDeckName();
            return new SimpleStringProperty(value == null ? "" : value);
        });
        nameCol.setMinWidth(200);
        // Null-safe, case-insensitive sort
        nameCol.setComparator((a, b) -> {
            if (a == null && b == null) {
                return 0;
            }
            if (a == null) {
                return -1;
            }
            if (b == null) {
                return 1;
            }
            return String.CASE_INSENSITIVE_ORDER.compare(a, b);
        });

        TableColumn<DeckFile, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cell -> {
            DeckFile deck = cell.getValue();
            String value = deck == null ? "" : deck.getDescription();
            return new SimpleStringProperty(value == null ? "" : value);
        });
        descCol.setMinWidth(220);

        table.getColumns().addAll(nameCol, descCol);

        table.setPrefHeight(240);
        table.setMinHeight(160);

        VBox.setVgrow(this, Priority.ALWAYS);
        getChildren().addAll(title, table);
    }

    public void setDecks(List<DeckFile> decks) {
        List<DeckFile> sorted = new ArrayList<>();
        if (decks != null) {
            sorted.addAll(decks);
        }
        // Sort A-Z ignoring case and extra spaces
        sorted.sort(Comparator.comparing(
            deck -> {
                String name = deck.getDeckName();
                return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
            }
        ));

        // Push sorted data into the table
        ObservableList<DeckFile> data = FXCollections.observableArrayList(sorted);
        table.setItems(data);
        table.getSortOrder().setAll(nameCol);
        table.sort();
    }
}
