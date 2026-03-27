package com.flashmaster.components;

import com.flashmaster.data.DataAccessLayer;
import com.flashmaster.data.DeckFile;
import com.flashmaster.data.FlashcardFile;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DefineFlashcardsView extends VBox {

    private static final List<String> STATUSES = List.of("New", "Learning", "Mastered");

    public DefineFlashcardsView(DataAccessLayer dataAccessLayer, Runnable onBack) {
        getStyleClass().add("content-page");
        setAlignment(Pos.TOP_LEFT);
        setSpacing(16);
        setPadding(new Insets(24));

        Map<String, DeckFile> deckLookup = loadDeckLookup(dataAccessLayer);

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        Label title = new Label("Define Flashcard");
        title.getStyleClass().add("section-title");

        HBox header = new HBox(12, backButton, title);
        header.setAlignment(Pos.CENTER_LEFT);

        Label deckLabel = new Label("Deck *");
        deckLabel.getStyleClass().add("form-label");

        ComboBox<String> deckCombo = new ComboBox<>();
        deckCombo.getStyleClass().add("form-input");
        deckCombo.setPromptText("Select a deck");
        deckCombo.setItems(FXCollections.observableArrayList(deckLookup.keySet()));
        deckCombo.setMaxWidth(Double.MAX_VALUE);

        Label deckError = createErrorLabel();

        Label frontLabel = new Label("Front Text *");
        frontLabel.getStyleClass().add("form-label");

        TextArea frontArea = new TextArea();
        frontArea.getStyleClass().add("form-textarea");
        frontArea.setPromptText("Enter the question or prompt");
        frontArea.setPrefRowCount(4);

        Label frontError = createErrorLabel();

        Label backLabel = new Label("Back Text *");
        backLabel.getStyleClass().add("form-label");

        TextArea backArea = new TextArea();
        backArea.getStyleClass().add("form-textarea");
        backArea.setPromptText("Enter the answer");
        backArea.setPrefRowCount(4);

        Label backError = createErrorLabel();

        Label statusLabel = new Label("Status *");
        statusLabel.getStyleClass().add("form-label");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getStyleClass().add("form-input");
        statusCombo.setItems(FXCollections.observableArrayList(STATUSES));
        statusCombo.setValue("New");
        statusCombo.setMaxWidth(Double.MAX_VALUE);

        Label statusError = createErrorLabel();

        Label creationDateLabel = new Label("Creation Date");
        creationDateLabel.getStyleClass().add("form-label");

        TextField creationDateField = new TextField("Auto-generated on save");
        creationDateField.getStyleClass().add("form-input");
        creationDateField.setEditable(false);
        creationDateField.setFocusTraversable(false);

        Label lastReviewDateLabel = new Label("Last Review Date");
        lastReviewDateLabel.getStyleClass().add("form-label");

        TextField lastReviewDateField = new TextField("Auto-generated on save");
        lastReviewDateField.getStyleClass().add("form-input");
        lastReviewDateField.setEditable(false);
        lastReviewDateField.setFocusTraversable(false);

        Label noDecksError = new Label("No decks found. Create a deck first.");
        noDecksError.getStyleClass().add("form-error");
        boolean hasDecks = !deckLookup.isEmpty();
        noDecksError.setVisible(!hasDecks);
        noDecksError.setManaged(!hasDecks);

        Label saveResult = new Label();
        saveResult.getStyleClass().add("form-success");
        saveResult.setVisible(false);
        saveResult.setManaged(false);

        Button saveButton = new Button("Save Flashcard");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setDisable(!hasDecks || dataAccessLayer == null);
        saveButton.setOnAction(event -> {
            saveResult.setVisible(false);
            saveResult.setManaged(false);

            clearError(deckError);
            clearError(frontError);
            clearError(backError);
            clearError(statusError);

            clearControlError(deckCombo);
            clearControlError(frontArea);
            clearControlError(backArea);
            clearControlError(statusCombo);

            String selectedDeckName = deckCombo.getValue();
            String frontText = trimValue(frontArea.getText());
            String backText = trimValue(backArea.getText());
            String status = statusCombo.getValue();

            boolean isValid = true;
            if (selectedDeckName == null || selectedDeckName.isBlank()) {
                showError(deckError, "Deck is required.");
                markControlError(deckCombo);
                isValid = false;
            }
            if (frontText.isEmpty()) {
                showError(frontError, "Front Text is required.");
                markControlError(frontArea);
                isValid = false;
            }
            if (backText.isEmpty()) {
                showError(backError, "Back Text is required.");
                markControlError(backArea);
                isValid = false;
            }
            if (status == null || status.isBlank()) {
                showError(statusError, "Status is required.");
                markControlError(statusCombo);
                isValid = false;
            }

            if (!isValid || dataAccessLayer == null) {
                return;
            }

            DeckFile selectedDeck = deckLookup.get(selectedDeckName);
            List<FlashcardFile> existingFlashcards = dataAccessLayer.getAllFlashcardFiles();
            boolean duplicateFrontText = existingFlashcards.stream().anyMatch(card ->
                    card.getDeckID() == selectedDeck.getDeckID()
                            && trimValue(card.getFrontText()).equalsIgnoreCase(frontText));

            if (duplicateFrontText) {
                showError(frontError, "Front Text must be unique within the selected deck.");
                markControlError(frontArea);
                return;
            }

            int nextFlashcardID = existingFlashcards.stream()
                    .map(FlashcardFile::getFlashcardID)
                    .max(Integer::compareTo)
                    .orElse(0) + 1;

            LocalDate today = LocalDate.now();
            FlashcardFile flashcardFile = new FlashcardFile(
                    nextFlashcardID,
                    selectedDeck.getDeckID(),
                    selectedDeck.getDeckName(),
                    frontText,
                    backText,
                    status,
                    today,
                    today);
            dataAccessLayer.addFlashcardFile(flashcardFile);

            frontArea.clear();
            backArea.clear();
            statusCombo.setValue("New");
            clearError(frontError);
            clearError(backError);
            clearControlError(frontArea);
            clearControlError(backArea);

            saveResult.setText("Flashcard saved.");
            saveResult.setVisible(true);
            saveResult.setManaged(true);
        });

        deckCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.isBlank()) {
                clearError(deckError);
                clearControlError(deckCombo);
            }
        });
        frontArea.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                clearError(frontError);
                clearControlError(frontArea);
            }
        });
        backArea.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                clearError(backError);
                clearControlError(backArea);
            }
        });
        statusCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.isBlank()) {
                clearError(statusError);
                clearControlError(statusCombo);
            }
        });

        VBox form = new VBox(
                12,
                deckLabel,
                deckCombo,
                deckError,
                frontLabel,
                frontArea,
                frontError,
                backLabel,
                backArea,
                backError,
                statusLabel,
                statusCombo,
                statusError,
                creationDateLabel,
                creationDateField,
                lastReviewDateLabel,
                lastReviewDateField,
                noDecksError,
                saveResult,
                saveButton);
        form.setAlignment(Pos.TOP_LEFT);
        form.setMaxWidth(620);

        getChildren().addAll(header, form);
    }

    private static Map<String, DeckFile> loadDeckLookup(DataAccessLayer dataAccessLayer) {
        Map<String, DeckFile> lookup = new LinkedHashMap<>();
        if (dataAccessLayer == null) {
            return lookup;
        }
        List<DeckFile> decks = dataAccessLayer.getAllDeckFiles();
        decks.sort(Comparator.comparing(
                deck -> {
                    String name = deck.getDeckName();
                    return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
                }));
        for (DeckFile deck : decks) {
            String key = deck.getDeckName();
            if (key != null && !key.isBlank()) {
                lookup.put(key, deck);
            }
        }
        return lookup;
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
}
