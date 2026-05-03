package com.flashmaster.components;

import com.flashmaster.data.DeckFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Decks table showing stored deck data.
 */
public class DecksTable extends VBox {

    private final TableView<DeckFile> table;
    private final TableColumn<DeckFile, String> nameCol;
    private Consumer<DeckFile> onDeckSelected;
    private Consumer<DeckFile> onDeckClicked;
    private Consumer<DeckFile> onDeckDoubleClicked;

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
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (onDeckSelected != null) {
                onDeckSelected.accept(newSelection);
            }
        });
        table.setRowFactory(tableView -> {
            TableRow<DeckFile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() != MouseButton.PRIMARY || row.isEmpty()) {
                    return;
                }
                if (event.getClickCount() == 1 && onDeckClicked != null) {
                    onDeckClicked.accept(row.getItem());
                }
                if (event.getClickCount() == 2 && onDeckDoubleClicked != null) {
                    onDeckDoubleClicked.accept(row.getItem());
                }
            });
            return row;
        });

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
            String value = deck == null ? "" : firstLine(deck.getDescription());
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

    public void setOnDeckSelected(Consumer<DeckFile> onDeckSelected) {
        this.onDeckSelected = onDeckSelected;
    }

    public void setOnDeckClicked(Consumer<DeckFile> onDeckClicked) {
        this.onDeckClicked = onDeckClicked;
    }

    public void setOnDeckDoubleClicked(Consumer<DeckFile> onDeckDoubleClicked) {
        this.onDeckDoubleClicked = onDeckDoubleClicked;
    }

    public DeckFile getSelectedDeck() {
        return table.getSelectionModel().getSelectedItem();
    }

    public void clearSelection() {
        table.getSelectionModel().clearSelection();
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
