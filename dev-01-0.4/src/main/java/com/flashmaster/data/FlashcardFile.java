package com.flashmaster.data;

import java.time.LocalDate;

public class FlashcardFile {
    private final int flashcardID;
    private final int deckID;
    private final String deckName;
    private final String frontText;
    private final String backText;
    private final String status;
    private final LocalDate creationDate;
    private final LocalDate lastReviewDate;

    public FlashcardFile(
            int flashcardID,
            int deckID,
            String deckName,
            String frontText,
            String backText,
            String status,
            LocalDate creationDate,
            LocalDate lastReviewDate) {
        this.flashcardID = flashcardID;
        this.deckID = deckID;
        this.deckName = deckName;
        this.frontText = frontText;
        this.backText = backText;
        this.status = status;
        this.creationDate = creationDate;
        this.lastReviewDate = lastReviewDate;
    }

    public int getFlashcardID() {
        return flashcardID;
    }

    public int getDeckID() {
        return deckID;
    }

    public String getDeckName() {
        return deckName;
    }

    public String getFrontText() {
        return frontText;
    }

    public String getBackText() {
        return backText;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getLastReviewDate() {
        return lastReviewDate;
    }

    public String toFileString() {
        return escapeField(String.valueOf(flashcardID)) + "|"
                + escapeField(String.valueOf(deckID)) + "|"
                + escapeField(deckName) + "|"
                + escapeField(frontText) + "|"
                + escapeField(backText) + "|"
                + escapeField(status) + "|"
                + escapeField(creationDate.toString()) + "|"
                + escapeField(lastReviewDate.toString());
    }

    public static FlashcardFile fromFileString(String line) {
        String[] parts = splitEscaped(line);
        if (parts.length != 8) {
            throw new IllegalArgumentException("Invalid file format: " + line);
        }

        int flashcardID = Integer.parseInt(unescapeField(parts[0]).trim());
        int deckID = Integer.parseInt(unescapeField(parts[1]).trim());
        String deckName = unescapeField(parts[2]).trim();
        String frontText = unescapeField(parts[3]).trim();
        String backText = unescapeField(parts[4]).trim();
        String status = unescapeField(parts[5]).trim();
        LocalDate creationDate = LocalDate.parse(unescapeField(parts[6]).trim());
        LocalDate lastReviewDate = LocalDate.parse(unescapeField(parts[7]).trim());

        return new FlashcardFile(
                flashcardID,
                deckID,
                deckName,
                frontText,
                backText,
                status,
                creationDate,
                lastReviewDate);
    }

    private static String escapeField(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private static String unescapeField(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder(value.length());
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (escaping) {
                if (ch == 'n') {
                    out.append('\n');
                } else if (ch == 'r') {
                    out.append('\r');
                } else {
                    out.append(ch);
                }
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                out.append(ch);
            }
        }
        if (escaping) {
            out.append('\\');
        }
        return out.toString();
    }

    private static String[] splitEscaped(String line) {
        if (line == null) {
            return new String[0];
        }
        StringBuilder current = new StringBuilder();
        java.util.List<String> parts = new java.util.ArrayList<>();
        boolean escaping = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (escaping) {
                current.append('\\').append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if (ch == '|') {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        if (escaping) {
            current.append('\\');
        }
        parts.add(current.toString());
        return parts.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return "FlashcardFile{"
                + "flashcardID=" + flashcardID
                + ", deckID=" + deckID
                + ", deckName='" + deckName + '\''
                + ", status='" + status + '\''
                + ", creationDate=" + creationDate
                + ", lastReviewDate=" + lastReviewDate
                + '}';
    }
}
