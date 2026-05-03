package com.flashmaster.components;

import com.flashmaster.data.DataAccessLayer;
import com.flashmaster.data.DeckFile;
import com.flashmaster.data.FlashcardFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DeckReviewView extends VBox {

    private static final String FILTER_ALL = "All";
    private static final List<String> FILTER_VALUES = List.of(FILTER_ALL, "New", "Learning", "Mastered");
    private static final List<String> STATUS_VALUES = List.of("New", "Learning", "Mastered");

    private final DataAccessLayer dataAccessLayer;
    private final DeckFile deck;

    private final ComboBox<String> filterField;
    private final TextField deckNameField;
    private final TextArea frontTextArea;
    private final TextArea backTextArea;
    private final ComboBox<String> statusField;
    private final TextField creationDateField;
    private final TextField lastReviewedDateField;
    private final Button previousButton;
    private final Button nextButton;
    private final Button saveButton;
    private final Label cardPositionLabel;
    private final Label emptyStateLabel;

    private final List<ReviewCardDraft> allDraftCards;
    private List<ReviewCardDraft> filteredDraftCards;
    private int currentIndex;

    public DeckReviewView(DataAccessLayer dataAccessLayer, DeckFile deck, Runnable onBack) {
        this.dataAccessLayer = dataAccessLayer;
        this.deck = deck;
        this.allDraftCards = new ArrayList<>();
        this.filteredDraftCards = new ArrayList<>();
        this.currentIndex = -1;

        getStyleClass().add("content-page");
        setAlignment(Pos.TOP_LEFT);
        setSpacing(16);
        setPadding(new Insets(24));

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(event -> {
            captureCurrentCardEdits();
            if (onBack != null) {
                onBack.run();
            }
        });

        Label title = new Label("Deck Review");
        title.getStyleClass().add("section-title");

        HBox header = new HBox(12, backButton, title);
        header.setAlignment(Pos.CENTER_LEFT);

        Label deckNameLabel = new Label("Deck Name");
        deckNameLabel.getStyleClass().add("form-label");

        deckNameField = new TextField(deck == null ? "" : safe(deck.getDeckName()));
        deckNameField.getStyleClass().add("form-input");
        deckNameField.setEditable(false);
        deckNameField.setFocusTraversable(false);

        Label filterLabel = new Label("Filter");
        filterLabel.getStyleClass().add("form-label");

        filterField = new ComboBox<>();
        filterField.getStyleClass().add("form-input");
        filterField.setItems(FXCollections.observableArrayList(FILTER_VALUES));
        filterField.setValue(FILTER_ALL);
        filterField.setMaxWidth(Double.MAX_VALUE);
        filterField.setOnAction(event -> applyFilterAndShow(getCurrentFlashcardId()));

        Label frontTextLabel = new Label("Front");
        frontTextLabel.getStyleClass().add("form-label");

        frontTextArea = new TextArea();
        frontTextArea.getStyleClass().add("form-textarea");
        frontTextArea.setPrefRowCount(4);

        Label backTextLabel = new Label("Back");
        backTextLabel.getStyleClass().add("form-label");

        backTextArea = new TextArea();
        backTextArea.getStyleClass().add("form-textarea");
        backTextArea.setPrefRowCount(4);

        Label statusLabel = new Label("Status");
        statusLabel.getStyleClass().add("form-label");

        statusField = new ComboBox<>();
        statusField.getStyleClass().add("form-input");
        statusField.setItems(FXCollections.observableArrayList(STATUS_VALUES));
        statusField.setMaxWidth(Double.MAX_VALUE);

        Label creationDateLabel = new Label("Creation Date");
        creationDateLabel.getStyleClass().add("form-label");

        creationDateField = new TextField();
        creationDateField.getStyleClass().add("form-input");
        creationDateField.setEditable(false);
        creationDateField.setFocusTraversable(false);

        Label lastReviewedDateLabel = new Label("Last Reviewed Date");
        lastReviewedDateLabel.getStyleClass().add("form-label");

        lastReviewedDateField = new TextField();
        lastReviewedDateField.getStyleClass().add("form-input");
        lastReviewedDateField.setEditable(false);
        lastReviewedDateField.setFocusTraversable(false);

        previousButton = new Button("Previous");
        previousButton.getStyleClass().add("secondary-button");
        previousButton.setOnAction(event -> showAdjacentCard(-1));

        nextButton = new Button("Next");
        nextButton.getStyleClass().add("secondary-button");
        nextButton.setOnAction(event -> showAdjacentCard(1));

        saveButton = new Button("Save");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(event -> saveChanges());

        HBox actions = new HBox(10, previousButton, nextButton, saveButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        cardPositionLabel = new Label();
        cardPositionLabel.getStyleClass().add("form-label");

        emptyStateLabel = new Label();
        emptyStateLabel.getStyleClass().add("form-label");
        emptyStateLabel.setVisible(false);
        emptyStateLabel.setManaged(false);

        VBox form = new VBox(
                10,
                deckNameLabel,
                deckNameField,
                filterLabel,
                filterField,
                frontTextLabel,
                frontTextArea,
                backTextLabel,
                backTextArea,
                statusLabel,
                statusField,
                creationDateLabel,
                creationDateField,
                lastReviewedDateLabel,
                lastReviewedDateField,
                cardPositionLabel,
                emptyStateLabel,
                actions
        );
        form.setAlignment(Pos.TOP_LEFT);
        form.setMaxWidth(700);

        getChildren().addAll(header, form);
        VBox.setVgrow(form, Priority.ALWAYS);

        loadDeckCards();
        applyFilterAndShow(null);
    }

    private void loadDeckCards() {
        allDraftCards.clear();
        if (dataAccessLayer == null || deck == null) {
            return;
        }

        for (FlashcardFile flashcard : dataAccessLayer.getAllFlashcardFiles()) {
            if (flashcard != null && flashcard.getDeckID() == deck.getDeckID()) {
                allDraftCards.add(new ReviewCardDraft(flashcard));
            }
        }

        allDraftCards.sort(
                Comparator.comparing(
                        ReviewCardDraft::getCreationDate,
                        Comparator.nullsLast(LocalDate::compareTo)
                ).thenComparingInt(ReviewCardDraft::getFlashcardId)
        );
    }

    private void applyFilterAndShow(Integer preferredFlashcardId) {
        captureCurrentCardEdits();

        String selectedFilter = normalizeFilter(filterField.getValue());
        filteredDraftCards = new ArrayList<>();
        for (ReviewCardDraft card : allDraftCards) {
            if (matchesFilter(card, selectedFilter)) {
                filteredDraftCards.add(card);
            }
        }

        if (filteredDraftCards.isEmpty()) {
            currentIndex = -1;
            showNoCardState();
            return;
        }

        int nextIndex = indexForFlashcard(preferredFlashcardId);
        if (nextIndex < 0) {
            nextIndex = 0;
        }
        currentIndex = nextIndex;
        showCurrentCard();
    }

    private int indexForFlashcard(Integer flashcardId) {
        if (flashcardId == null) {
            return -1;
        }
        for (int i = 0; i < filteredDraftCards.size(); i++) {
            if (filteredDraftCards.get(i).getFlashcardId() == flashcardId) {
                return i;
            }
        }
        return -1;
    }

    private Integer getCurrentFlashcardId() {
        if (currentIndex < 0 || currentIndex >= filteredDraftCards.size()) {
            return null;
        }
        return filteredDraftCards.get(currentIndex).getFlashcardId();
    }

    private static boolean matchesFilter(ReviewCardDraft card, String filterValue) {
        if (card == null) {
            return false;
        }
        if (FILTER_ALL.equalsIgnoreCase(filterValue)) {
            return true;
        }
        return normalize(card.getStatus()).equals(normalize(filterValue));
    }

    private static String normalizeFilter(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            return FILTER_ALL;
        }
        return normalized;
    }

    private void showCurrentCard() {
        if (currentIndex < 0 || currentIndex >= filteredDraftCards.size()) {
            showNoCardState();
            return;
        }

        ReviewCardDraft card = filteredDraftCards.get(currentIndex);
        card.setReviewed(true);

        frontTextArea.setText(safe(card.getFrontText()));
        backTextArea.setText(safe(card.getBackText()));
        statusField.setValue(canonicalStatus(card.getStatus()));
        creationDateField.setText(card.getCreationDate() == null ? "" : card.getCreationDate().toString());
        lastReviewedDateField.setText(LocalDate.now().toString());

        setCardFieldsDisabled(false);
        previousButton.setDisable(currentIndex <= 0);
        nextButton.setDisable(currentIndex >= filteredDraftCards.size() - 1);
        saveButton.setDisable(dataAccessLayer == null || allDraftCards.isEmpty());

        cardPositionLabel.setText("Flashcard " + (currentIndex + 1) + " of " + filteredDraftCards.size());
        emptyStateLabel.setVisible(false);
        emptyStateLabel.setManaged(false);
    }

    private void showNoCardState() {
        frontTextArea.clear();
        backTextArea.clear();
        statusField.setValue(null);
        creationDateField.clear();
        lastReviewedDateField.setText(LocalDate.now().toString());

        setCardFieldsDisabled(true);
        previousButton.setDisable(true);
        nextButton.setDisable(true);
        saveButton.setDisable(dataAccessLayer == null || allDraftCards.isEmpty());

        cardPositionLabel.setText("No flashcards available for this filter.");
        emptyStateLabel.setText("Try another filter value.");
        emptyStateLabel.setVisible(true);
        emptyStateLabel.setManaged(true);
    }

    private void setCardFieldsDisabled(boolean disabled) {
        frontTextArea.setDisable(disabled);
        backTextArea.setDisable(disabled);
        statusField.setDisable(disabled);
    }

    private void showAdjacentCard(int delta) {
        if (filteredDraftCards.isEmpty()) {
            return;
        }

        int targetIndex = currentIndex + delta;
        if (targetIndex < 0 || targetIndex >= filteredDraftCards.size()) {
            return;
        }

        captureCurrentCardEdits();
        currentIndex = targetIndex;
        showCurrentCard();
    }

    private void captureCurrentCardEdits() {
        if (currentIndex < 0 || currentIndex >= filteredDraftCards.size()) {
            return;
        }

        ReviewCardDraft card = filteredDraftCards.get(currentIndex);
        card.setFrontText(safe(frontTextArea.getText()));
        card.setBackText(safe(backTextArea.getText()));

        String selectedStatus = statusField.getValue();
        if (selectedStatus != null && !selectedStatus.isBlank()) {
            card.setStatus(selectedStatus.trim());
        }
    }

    private void saveChanges() {
        if (dataAccessLayer == null || allDraftCards.isEmpty()) {
            return;
        }

        captureCurrentCardEdits();

        ValidationResult validation = validateDraftCards();
        if (!validation.isValid()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", validation.message());
            focusCard(validation.flashcardId());
            return;
        }

        LocalDate today = LocalDate.now();
        int updatedCount = 0;

        for (ReviewCardDraft card : allDraftCards) {
            if (!card.isReviewed() && !card.hasFieldChanges()) {
                continue;
            }

            FlashcardFile updatedFlashcard = card.toFlashcardFile(today);
            boolean updated = dataAccessLayer.updateFlashcard(updatedFlashcard);
            if (!updated) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Save Failed",
                        "Could not save flashcard with ID " + card.getFlashcardId() + "."
                );
                return;
            }
            updatedCount++;
        }

        if (updatedCount == 0) {
            showAlert(Alert.AlertType.INFORMATION, "No Changes", "No updates to save.");
            return;
        }

        Integer selectedFlashcardId = getCurrentFlashcardId();
        String activeFilter = normalizeFilter(filterField.getValue());

        loadDeckCards();
        filterField.setValue(activeFilter);
        applyFilterAndShow(selectedFlashcardId);

        showAlert(Alert.AlertType.INFORMATION, "Saved", "Saved " + updatedCount + " flashcard(s).");
    }

    private ValidationResult validateDraftCards() {
        for (ReviewCardDraft card : allDraftCards) {
            if (trim(card.getFrontText()).isEmpty()) {
                return ValidationResult.failure(card.getFlashcardId(), "Front text cannot be empty.");
            }
            if (trim(card.getBackText()).isEmpty()) {
                return ValidationResult.failure(card.getFlashcardId(), "Back text cannot be empty.");
            }
            if (!isValidStatus(card.getStatus())) {
                return ValidationResult.failure(card.getFlashcardId(), "Status must be New, Learning, or Mastered.");
            }
        }

        Map<String, Integer> seenFrontTexts = new HashMap<>();
        for (ReviewCardDraft card : allDraftCards) {
            String normalizedFront = normalize(trim(card.getFrontText()));
            Integer existingFlashcardId = seenFrontTexts.putIfAbsent(normalizedFront, card.getFlashcardId());
            if (existingFlashcardId != null) {
                return ValidationResult.failure(
                        card.getFlashcardId(),
                        "Front text must be unique within this deck (trimmed value)."
                );
            }
        }

        return ValidationResult.success();
    }

    private void focusCard(int flashcardId) {
        if (flashcardId <= 0) {
            return;
        }

        if (indexForFlashcard(flashcardId) < 0) {
            filterField.setValue(FILTER_ALL);
            applyFilterAndShow(flashcardId);
            return;
        }

        currentIndex = indexForFlashcard(flashcardId);
        showCurrentCard();
    }

    private static boolean isValidStatus(String value) {
        String normalized = normalize(value);
        for (String status : STATUS_VALUES) {
            if (normalize(status).equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    private static String canonicalStatus(String value) {
        String normalized = normalize(value);
        for (String status : STATUS_VALUES) {
            if (normalize(status).equals(normalized)) {
                return status;
            }
        }
        return STATUS_VALUES.get(0);
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private record ValidationResult(boolean isValid, int flashcardId, String message) {
        private static ValidationResult success() {
            return new ValidationResult(true, -1, "");
        }

        private static ValidationResult failure(int flashcardId, String message) {
            return new ValidationResult(false, flashcardId, message);
        }
    }

    private static class ReviewCardDraft {
        private final int flashcardId;
        private final int deckId;
        private final String deckName;
        private final LocalDate creationDate;
        private final String originalFrontText;
        private final String originalBackText;
        private final String originalStatus;
        private final LocalDate originalLastReviewDate;

        private String frontText;
        private String backText;
        private String status;
        private boolean reviewed;

        private ReviewCardDraft(FlashcardFile flashcard) {
            this.flashcardId = flashcard.getFlashcardID();
            this.deckId = flashcard.getDeckID();
            this.deckName = safe(flashcard.getDeckName());
            this.creationDate = flashcard.getCreationDate();
            this.originalFrontText = safe(flashcard.getFrontText());
            this.originalBackText = safe(flashcard.getBackText());
            this.originalStatus = safe(flashcard.getStatus());
            this.originalLastReviewDate = flashcard.getLastReviewDate();
            this.frontText = this.originalFrontText;
            this.backText = this.originalBackText;
            this.status = canonicalStatus(this.originalStatus);
            this.reviewed = false;
        }

        private int getFlashcardId() {
            return flashcardId;
        }

        private LocalDate getCreationDate() {
            return creationDate;
        }

        private String getFrontText() {
            return frontText;
        }

        private void setFrontText(String frontText) {
            this.frontText = safe(frontText);
        }

        private String getBackText() {
            return backText;
        }

        private void setBackText(String backText) {
            this.backText = safe(backText);
        }

        private String getStatus() {
            return status;
        }

        private void setStatus(String status) {
            this.status = canonicalStatus(status);
        }

        private boolean isReviewed() {
            return reviewed;
        }

        private void setReviewed(boolean reviewed) {
            this.reviewed = reviewed;
        }

        private boolean hasFieldChanges() {
            return !trim(frontText).equals(trim(originalFrontText))
                    || !trim(backText).equals(trim(originalBackText))
                    || !normalize(status).equals(normalize(originalStatus));
        }

        private FlashcardFile toFlashcardFile(LocalDate reviewDate) {
            LocalDate persistedReviewDate = reviewDate == null ? originalLastReviewDate : reviewDate;
            return new FlashcardFile(
                    flashcardId,
                    deckId,
                    deckName,
                    trim(frontText),
                    trim(backText),
                    canonicalStatus(status),
                    creationDate,
                    persistedReviewDate
            );
        }
    }
}
