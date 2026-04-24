package com.flashmaster.components;

import com.flashmaster.data.DataAccessLayer;
import com.flashmaster.data.DeckFile;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ListDecksView extends VBox {

    private final DataAccessLayer dataAccessLayer;
    private final DecksTable decksTable;
    private final Button deleteButton;
    private final Button storeButton;
    private final TextField nameField;
    private final TextArea descriptionArea;
    private final Label nameError;
    private final Label editHint;

    private DeckFile selectedDeck;
    private DeckFile editingDeck;

    public ListDecksView(DataAccessLayer dataAccessLayer, Runnable onBack) {
        this.dataAccessLayer = dataAccessLayer;
        this.selectedDeck = null;
        this.editingDeck = null;

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

        decksTable = new DecksTable("All Decks");
        VBox.setVgrow(decksTable, Priority.ALWAYS);

        deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("secondary-button");
        deleteButton.setDisable(true);
        deleteButton.setOnAction(event -> deleteSelectedDeck());

        Button cancelEditButton = new Button("Cancel Edit");
        cancelEditButton.getStyleClass().add("secondary-button");
        cancelEditButton.setOnAction(event -> clearEditForm());

        HBox actions = new HBox(10, deleteButton, cancelEditButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        Label editTitle = new Label("Edit Deck");
        editTitle.getStyleClass().add("section-title");

        editHint = new Label("Select a row, then double-click it to edit.");
        editHint.getStyleClass().add("form-label");

        Label nameLabel = new Label("Deck Name *");
        nameLabel.getStyleClass().add("form-label");

        nameField = new TextField();
        nameField.getStyleClass().add("form-input");
        nameField.setPromptText("e.g., CS151 - Design Patterns");

        nameError = createErrorLabel();

        Label descriptionLabel = new Label("Description");
        descriptionLabel.getStyleClass().add("form-label");

        descriptionArea = new TextArea();
        descriptionArea.getStyleClass().add("form-textarea");
        descriptionArea.setPromptText("Optional description");
        descriptionArea.setPrefRowCount(4);

        storeButton = new Button("Store Deck");
        storeButton.getStyleClass().add("primary-button");
        storeButton.setDisable(true);
        storeButton.setOnAction(event -> storeDeckEdits());

        VBox editForm = new VBox(
                10,
                editTitle,
                editHint,
                nameLabel,
                nameField,
                nameError,
                descriptionLabel,
                descriptionArea,
                storeButton
        );
        editForm.setMaxWidth(620);

        nameField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                clearError(nameError);
                clearControlError(nameField);
            }
        });

        decksTable.setOnDeckSelected(deck -> {
            selectedDeck = deck;
            deleteButton.setDisable(selectedDeck == null);
        });
        decksTable.setOnDeckDoubleClicked(this::startEditingDeck);

        reloadDecks();

        getChildren().addAll(header, decksTable, actions, editForm);
    }

    private void reloadDecks() {
        if (dataAccessLayer == null) {
            decksTable.setDecks(List.of());
            return;
        }
        decksTable.setDecks(dataAccessLayer.getAllDeckFiles());
    }

    private void deleteSelectedDeck() {
        if (selectedDeck == null || dataAccessLayer == null) {
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Deck");
        confirmAlert.setHeaderText("Delete selected deck?");
        confirmAlert.setContentText(
                "Deck: " + safe(selectedDeck.getDeckName())
                        + "\n\nAll flashcards linked to this deck will also be deleted permanently."
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        boolean deleted = dataAccessLayer.deleteDeck(selectedDeck);
        if (deleted) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Deleted");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Deck and linked flashcards were deleted.");
            successAlert.showAndWait();

            selectedDeck = null;
            deleteButton.setDisable(true);
            decksTable.clearSelection();

            clearEditForm();
            reloadDecks();
            return;
        }

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Delete Failed");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText("Could not delete the selected deck.");
        errorAlert.showAndWait();
    }

    private void startEditingDeck(DeckFile deck) {
        if (deck == null) {
            return;
        }

        editingDeck = deck;
        nameField.setText(safe(deck.getDeckName()));
        descriptionArea.setText(safe(deck.getDescription()));
        editHint.setText("Editing deck \"" + safe(deck.getDeckName()) + "\". Update fields and click Store Deck.");
        storeButton.setDisable(false);

        clearError(nameError);
        clearControlError(nameField);
    }

    private void storeDeckEdits() {
        if (editingDeck == null || dataAccessLayer == null) {
            return;
        }

        String deckName = trimValue(nameField.getText());
        String description = trimValue(descriptionArea.getText());

        clearError(nameError);
        clearControlError(nameField);

        if (deckName.isEmpty()) {
            showError(nameError, "Deck Name is required.");
            markControlError(nameField);
            return;
        }

        List<DeckFile> existingDecks = dataAccessLayer.getAllDeckFiles();
        boolean duplicateName = existingDecks.stream().anyMatch(deck ->
                deck.getDeckID() != editingDeck.getDeckID()
                        && trimValue(deck.getDeckName()).equalsIgnoreCase(deckName));
        if (duplicateName) {
            showError(nameError, "Deck Name must be unique.");
            markControlError(nameField);
            return;
        }

        DeckFile updatedDeck = new DeckFile(editingDeck.getDeckID(), deckName, description);
        boolean updated = dataAccessLayer.updateDeck(updatedDeck);
        if (updated) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Saved");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Deck changes were stored.");
            successAlert.showAndWait();

            selectedDeck = null;
            deleteButton.setDisable(true);
            decksTable.clearSelection();

            clearEditForm();
            reloadDecks();
            return;
        }

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Save Failed");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText("Could not store deck changes.");
        errorAlert.showAndWait();
    }

    private void clearEditForm() {
        editingDeck = null;
        nameField.clear();
        descriptionArea.clear();
        editHint.setText("Select a row, then double-click it to edit.");
        storeButton.setDisable(true);
        clearError(nameError);
        clearControlError(nameField);
    }

    private static Label createErrorLabel() {
        Label error = new Label();
        error.getStyleClass().add("form-error");
        error.setVisible(false);
        error.setManaged(false);
        return error;
    }

    private static void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private static void clearError(Label errorLabel) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private static void markControlError(Control control) {
        if (!control.getStyleClass().contains("input-error")) {
            control.getStyleClass().add("input-error");
        }
    }

    private static void clearControlError(Control control) {
        control.getStyleClass().remove("input-error");
    }

    private static String trimValue(String value) {
        return value == null ? "" : value.trim();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
