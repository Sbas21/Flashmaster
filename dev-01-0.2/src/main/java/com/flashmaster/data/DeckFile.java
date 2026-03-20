package com.flashmaster.data;

public class DeckFile {
    private int deckID;
    private String deckName;
    private String description;

    public DeckFile(int deckID, String deckName, String description) {
        this.deckID = deckID;
        this.deckName = deckName;
        this.description = description;
    }

    public int getDeckID() {
        return deckID;
    }

    public String getDeckName() {
        return deckName;
    }

    public String getDescription() {
        return description;
    }

    public String toFileString() {
        return deckID + "|" + deckName + "|" + description;
    }

    public static DeckFile fromFileString(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid file format: " + line);
        }

        int deckID = Integer.parseInt(parts[0].trim());
        String deckName = parts[1].trim();
        String description = parts[2].trim();

        return new DeckFile(deckID, deckName, description);
    }

    @Override
    public String toString() {
        return "DeckFile{" +
                "deckID=" + deckID +
                ", deckName='" + deckName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}