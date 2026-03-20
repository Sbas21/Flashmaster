package com.flashmaster.data;

public class DeckFile {
    private int uniqueId;
    private String deckName;
    private String description;

    public DeckFile(int uniqueId, String deckName, String description) {
        this.uniqueId = uniqueId;
        this.deckName = deckName;
        this.description = description;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public String getDeckName() {
        return deckName;
    }

    public String getDescription() {
        return description;
    }

    public String toFileString() {
        return uniqueId + "|" + deckName + "|" + description;
    }

    public static DeckFile fromFileString(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid file format: " + line);
        }

        int uniqueId = Integer.parseInt(parts[0].trim());
        String deckName = parts[1].trim();
        String description = parts[2].trim();

        return new DeckFile(uniqueId, deckName, description);
    }

    @Override
    public String toString() {
        return "DeckFile{" +
                "uniqueId=" + uniqueId +
                ", deckName='" + deckName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}