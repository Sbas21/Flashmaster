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
        return escapeField(String.valueOf(deckID)) + "|"
                + escapeField(deckName) + "|"
                + escapeField(description);
    }

    public static DeckFile fromFileString(String line) {
        String[] parts = splitEscaped(line);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid file format: " + line);
        }

        int deckID = Integer.parseInt(unescapeField(parts[0]).trim());
        String deckName = unescapeField(parts[1]).trim();
        String description = unescapeField(parts[2]).trim();

        return new DeckFile(deckID, deckName, description);
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
        return "DeckFile{" +
                "deckID=" + deckID +
                ", deckName='" + deckName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
