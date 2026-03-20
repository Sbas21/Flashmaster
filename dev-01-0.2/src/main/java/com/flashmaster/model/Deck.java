package com.flashmaster.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Data model for a flashcard deck, used by the DecksTable TableView.
 */
public class Deck {

    private final StringProperty name;
    private final StringProperty description;
    private final IntegerProperty totalFlashcards;

    public Deck(String name, String description, int totalFlashcards) {
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.totalFlashcards = new SimpleIntegerProperty(totalFlashcards);
    }

    // --- Name ---
    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    // --- Description ---
    public StringProperty descriptionProperty() {
        return description;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    // --- Total Flashcards ---
    public IntegerProperty totalFlashcardsProperty() {
        return totalFlashcards;
    }

    public int getTotalFlashcards() {
        return totalFlashcards.get();
    }

    public void setTotalFlashcards(int totalFlashcards) {
        this.totalFlashcards.set(totalFlashcards);
    }
}
