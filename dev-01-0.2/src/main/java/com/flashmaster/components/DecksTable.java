package com.flashmaster.components;

import com.flashmaster.model.Deck;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Decks table with three columns and sample data.
 * Mirrors the React DecksTable component.
 */
public class DecksTable extends VBox {

    public DecksTable() {
        getStyleClass().add("decks-table-container");
        setSpacing(16);

        // --- Title ---
        Label title = new Label("All Decks");
        title.getStyleClass().add("section-title");

        // --- Table ---
        TableView<Deck> table = new TableView<>();
        table.getStyleClass().add("decks-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Column: Deck Name
        TableColumn<Deck, String> nameCol = new TableColumn<>("Deck Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(200);

        // Column: Description
        TableColumn<Deck, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setMinWidth(200);

        // Column: Total Flashcards
        TableColumn<Deck, Integer> totalCol = new TableColumn<>("Total Flashcards");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalFlashcards"));
        totalCol.setMinWidth(130);

        table.getColumns().addAll(nameCol, descCol, totalCol);

        // --- Sample Data (matching React source) ---
        ObservableList<Deck> data = FXCollections.observableArrayList(
            new Deck("CS151 – Design Patterns", "OOP and MVC concepts", 12),
            new Deck("Spanish Vocabulary", "Basic verbs and nouns", 25)
        );
        table.setItems(data);

        // Size the table to fit its content reasonably
        table.setPrefHeight(160);
        table.setMinHeight(120);

        VBox.setVgrow(this, Priority.ALWAYS);
        getChildren().addAll(title, table);
    }
}
