package com.flashmaster.components;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


import javafx.collections.FXCollections;
import javafx.scene.control.*;

import com.flashmaster.data.DataAccessLayer;
import com.flashmaster.data.FlashcardFile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SearchFlashcardsView extends VBox {

    private static final List<String> STATUSES = List.of("New", "Learning", "Mastered");

    private final DataAccessLayer dataAccessLayer;
    private final TextField searchField;
    private final Label resultSummary;
    private final FlashcardsTable flashcardsTable;
    private List<FlashcardFile> allFlashcards;
    private final Button deleteButton;

    private final Button storeButton;
    private final Label editHint;
    private final Label deckNameError;
    private final Label frontTextError;
    private final Label backTextError;
    private final Label statusError;

    private final TextField deckNameField;
    private final ComboBox<String> statusField;
    private final TextField creationDateField;
    private final TextField lastReviewDateField;
    private final TextArea frontTextArea;
    private final TextArea backTextArea;

    private List<FlashcardFile> filteredFlashcards;
    private FlashcardFile selectedFlashcard;
    private FlashcardFile editingFlashcard;

    public SearchFlashcardsView(DataAccessLayer dataAccessLayer, Runnable onBack) {

        
        this.dataAccessLayer = dataAccessLayer;
        this.allFlashcards = new ArrayList<>();
        this.filteredFlashcards = new ArrayList<>();
        this.selectedFlashcard = null;
        this.editingFlashcard = null;

        getStyleClass().add("content-page");

        VBox content = new VBox();
        content.setAlignment(Pos.TOP_LEFT);
        content.setSpacing(16);
        content.setPadding(new Insets(24));

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

        

        deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("secondary-button");
        deleteButton.setDisable(true);
        deleteButton.setOnAction(event -> deleteSelectedFlashcard());

        flashcardsTable = new FlashcardsTable("Flashcard Results");
        flashcardsTable.setOnFlashcardSelected(flashcard -> {
            selectedFlashcard = flashcard;
            deleteButton.setDisable(selectedFlashcard == null);
        });

        flashcardsTable.setOnFlashcardDoubleClicked(this::startEditingFlashcard);

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> {
            searchField.clear();
            selectedFlashcard = null;
            flashcardsTable.clearSelection();
            deleteButton.setDisable(true);
            clearEditForm();
            applyCurrentFilter();
        });

        HBox searchControls = new HBox(10, searchField, searchButton, clearButton);
        searchControls.setAlignment(Pos.CENTER_LEFT);

        resultSummary = new Label();
        resultSummary.getStyleClass().add("form-label");

        VBox.setVgrow(flashcardsTable, Priority.ALWAYS);

        Button cancelEditButton = new Button("Cancel Edit");
        cancelEditButton.getStyleClass().add("secondary-button");
        cancelEditButton.setOnAction(event -> clearEditForm());

        HBox actions = new HBox(10, deleteButton, cancelEditButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        Label editTitle = new Label("Edit Flashcard");
        editTitle.getStyleClass().add("section-title");

        editHint = new Label("Select a row, then double-click it to edit.");
        editHint.getStyleClass().add("form-label");

        Label deckNameLabel = new Label("Deck Name *");
        deckNameLabel.getStyleClass().add("form-label");

        deckNameField = new TextField();
        deckNameField.getStyleClass().add("form-input");
        deckNameField.setPromptText("e.g., CS151");

        deckNameError = createErrorLabel();

        Label frontTextLabel = new Label("Front Text *");
        frontTextLabel.getStyleClass().add("form-label");

        frontTextArea = new TextArea();
        frontTextArea.getStyleClass().add("form-textarea");
        frontTextArea.setPromptText("Enter the front text");
        frontTextArea.setPrefRowCount(3);

        frontTextError = createErrorLabel();

        Label backTextLabel = new Label("Back Text *");
        backTextLabel.getStyleClass().add("form-label");

        backTextArea = new TextArea();
        backTextArea.getStyleClass().add("form-textarea");
        backTextArea.setPromptText("Enter the back text");
        backTextArea.setPrefRowCount(4);

        backTextError = createErrorLabel();

        Label statusLabel = new Label("Status *");
        statusLabel.getStyleClass().add("form-label");

        statusField = new ComboBox<>();
        statusField.getStyleClass().add("form-input");
        statusField.setItems(FXCollections.observableArrayList(STATUSES));
        statusField.setPromptText("Select status");
        statusField.setMaxWidth(Double.MAX_VALUE);
        statusField.setDisable(true);


        statusError = createErrorLabel();

        Label creationDateLabel = new Label("Creation Date");
        creationDateLabel.getStyleClass().add("form-label");

        creationDateField = new TextField();
        creationDateField.getStyleClass().add("form-input");
        creationDateField.setEditable(false);

        Label lastReviewDateLabel = new Label("Last Review Date");
        lastReviewDateLabel.getStyleClass().add("form-label");

        lastReviewDateField = new TextField();
        lastReviewDateField.getStyleClass().add("form-input");
        lastReviewDateField.setEditable(false);

        storeButton = new Button("Store Flashcard");
        storeButton.getStyleClass().add("primary-button");
        storeButton.setDisable(true);
        storeButton.setOnAction(event -> storeFlashcardEdits());

        VBox editForm = new VBox(
                10,
                editTitle,
                editHint,
                deckNameLabel,
                deckNameField,
                deckNameError,
                frontTextLabel,
                frontTextArea,
                frontTextError,
                backTextLabel,
                backTextArea,
                backTextError,
                statusLabel,
                statusField,
                statusError,
                creationDateLabel,
                creationDateField,
                lastReviewDateLabel,
                lastReviewDateField,
                storeButton
        );
        editForm.setMaxWidth(620);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyCurrentFilter());
        searchField.setOnAction(event -> applyCurrentFilter());

        deckNameField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                clearError(deckNameError);
                clearControlError(deckNameField);
            }
        });

        frontTextArea.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                clearError(frontTextError);
                clearControlError(frontTextArea);
            }
        });

        backTextArea.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                clearError(backTextError);
                clearControlError(backTextArea);
            }
        });

        statusField.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.isBlank()) {
                clearError(statusError);
                clearControlError(statusField);
            }
        });

        selectedFlashcard = null;
        editingFlashcard = null;
        deleteButton.setDisable(true);
        flashcardsTable.clearSelection();

        clearEditForm();
        reloadFlashcards();
        applyCurrentFilter();
        clearEditForm();

        content.getChildren().addAll(header, searchControls, resultSummary, flashcardsTable, actions, editForm);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);}

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

        filteredFlashcards = filtered;

        flashcardsTable.setFlashcards(filtered);
        if (query.isEmpty()) {
            resultSummary.setText("Showing all flashcards: " + filtered.size());
        } else {
            resultSummary.setText("Matches: " + filtered.size());
        }
    }

    private void deleteSelectedFlashcard() {
        if (selectedFlashcard == null || dataAccessLayer == null) {
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Flashcard");
        confirmAlert.setHeaderText("Delete selected flashcard?");
        confirmAlert.setContentText(
                "Deck: " + safe(selectedFlashcard.getDeckName())
                        + "\nFront: " + safe(selectedFlashcard.getFrontText())
                        + "\n\nThis action cannot be undone."
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        boolean deleted = dataAccessLayer.deleteFlashcard(selectedFlashcard);

        if (deleted) {
            reloadFlashcards();
            applyCurrentFilter();

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Deleted");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Flashcard deleted successfully.");
            successAlert.showAndWait();
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Delete Failed");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Could not delete the selected flashcard.");
            errorAlert.showAndWait();
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

    private void startEditingFlashcard(FlashcardFile flashcard) {
        if (flashcard == null) {
            return;
        }

        editingFlashcard = flashcard;
        selectedFlashcard = flashcard;
        deleteButton.setDisable(false);

        deckNameField.setText(safe(flashcard.getDeckName()));
        frontTextArea.setText(safe(flashcard.getFrontText()));
        backTextArea.setText(safe(flashcard.getBackText()));
        statusField.setValue(safe(flashcard.getStatus()));
        creationDateField.setText(
                flashcard.getCreationDate() == null ? "" : flashcard.getCreationDate().toString()
        );
        lastReviewDateField.setText(
                flashcard.getLastReviewDate() == null ? "" : flashcard.getLastReviewDate().toString()
        );

        editHint.setText("Editing selected flashcard. Update fields and click Store Flashcard.");

        clearError(deckNameError);
        clearError(frontTextError);
        clearError(backTextError);
        clearControlError(deckNameField);
        clearControlError(frontTextArea);
        clearControlError(backTextArea);
        clearError(statusError);
        clearControlError(statusField);

        statusField.setDisable(false);
        storeButton.setDisable(false);
    }
    private void clearEditForm() {
        editingFlashcard = null;

        deckNameField.clear();
        frontTextArea.clear();
        backTextArea.clear();
        statusField.setValue(null);
        creationDateField.clear();
        lastReviewDateField.clear();

        editHint.setText("Select a row, then double-click it to edit.");
        storeButton.setDisable(true);

        clearError(deckNameError);
        clearError(frontTextError);
        clearError(backTextError);
        clearControlError(deckNameField);
        clearControlError(frontTextArea);
        clearControlError(backTextArea);
        clearError(statusError);
        clearControlError(statusField);
    }
    private void storeFlashcardEdits() {
        if (editingFlashcard == null || dataAccessLayer == null) {
            return;
        }

        String deckName = trimValue(deckNameField.getText());
        String frontText = trimValue(frontTextArea.getText());
        String backText = trimValue(backTextArea.getText());
        String status = statusField.getValue() == null ? "" : statusField.getValue().trim();

        clearError(deckNameError);
        clearError(frontTextError);
        clearError(backTextError);
        clearError(statusError);
        clearControlError(deckNameField);
        clearControlError(frontTextArea);
        clearControlError(backTextArea);

        boolean hasError = false;

        if (deckName.isEmpty()) {
            showError(deckNameError, "Deck Name is required.");
            markControlError(deckNameField);
            hasError = true;
        }

        if (frontText.isEmpty()) {
            showError(frontTextError, "Front Text is required.");
            markControlError(frontTextArea);
            hasError = true;
        }

        if (backText.isEmpty()) {
            showError(backTextError, "Back Text is required.");
            markControlError(backTextArea);
            hasError = true;
        }

        if (status.isEmpty()) {
            showError(statusError, "Status is required.");
            markControlError(statusField);
            hasError = true;
        }

        if (hasError) {
            return;
        }

        FlashcardFile updatedFlashcard = new FlashcardFile(
                editingFlashcard.getFlashcardID(),
                editingFlashcard.getDeckID(),
                deckName,
                frontText,
                backText,
                status,
                editingFlashcard.getCreationDate(),
                editingFlashcard.getLastReviewDate()
        );

        boolean updated = dataAccessLayer.updateFlashcard(updatedFlashcard);
        if (updated) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Saved");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Flashcard changes were stored.");
            successAlert.showAndWait();

            selectedFlashcard = null;
            deleteButton.setDisable(true);
            flashcardsTable.clearSelection();

            clearEditForm();
            reloadFlashcards();
            applyCurrentFilter();
            return;
        }

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Save Failed");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText("Could not store flashcard changes.");
        errorAlert.showAndWait();
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

    private static boolean contains(String value, String query) {
        return normalize(value).contains(query);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
