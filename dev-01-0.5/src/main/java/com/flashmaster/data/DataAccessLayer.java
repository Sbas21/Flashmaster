package com.flashmaster.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class DataAccessLayer {

    private static final String DATA_FOLDER = "data";
    private static final Path DATA_DIRECTORY = Paths.get(DATA_FOLDER);
    private static final Path DECKS_FILE_PATH = DATA_DIRECTORY.resolve("decks.csv");
    private static final Path FLASHCARDS_FILE_PATH = DATA_DIRECTORY.resolve("flashcards.csv");
    private static final String DECKS_HEADER = "Deck ID|Deck Name|Description";
    private static final String FLASHCARDS_HEADER =
            "Flashcard ID|Deck ID|Deck Name|Front Text|Back Text|Status|Creation Date|Last Review Date";

    public DataAccessLayer() {
        initializeFiles();
    }

    private void initializeFiles() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            initializeFile(DECKS_FILE_PATH, DECKS_HEADER);
            initializeFile(FLASHCARDS_FILE_PATH, FLASHCARDS_HEADER);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize data files.", e);
        }
    }

    private void initializeFile(Path filePath, String header) throws IOException {
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        ensureHeaderExists(filePath, header);
    }

    private void ensureHeaderExists(Path filePath, String header) {
        try {
            if (!Files.exists(filePath)) {
                initializeFile(filePath, header);
                return;
            }
            List<String> lines = Files.readAllLines(filePath);
            if (lines.isEmpty()) {
                writeHeaderOnly(filePath, header);
                return;
            }

            String firstLine = lines.get(0).trim();
            if (!firstLine.equalsIgnoreCase(header)) {
                List<String> updatedLines = new ArrayList<>();
                updatedLines.add(header);
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        updatedLines.add(line);
                    }
                }
                Files.write(filePath, updatedLines, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not verify header for: " + filePath, e);
        }
    }

    private void writeHeaderOnly(Path filePath, String header) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            writer.write(header);
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Could not write header to: " + filePath, e);
        }
    }

    public void addDeckFile(DeckFile deckFile) {
        ensureHeaderExists(DECKS_FILE_PATH, DECKS_HEADER);

        try (BufferedWriter writer = Files.newBufferedWriter(
                DECKS_FILE_PATH,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(deckFile.toFileString());
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Could not write deck to file.", e);
        }
    }

    public List<DeckFile> getAllDeckFiles() {
        ensureHeaderExists(DECKS_FILE_PATH, DECKS_HEADER);

        List<DeckFile> deckFiles = new ArrayList<>();

        try {
            if (!Files.exists(DECKS_FILE_PATH)) {
                return deckFiles;
            }

            List<String> lines = Files.readAllLines(DECKS_FILE_PATH);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    deckFiles.add(DeckFile.fromFileString(line));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not read deck file.", e);
        }

        return deckFiles;
    }

    public void addFlashcardFile(FlashcardFile flashcardFile) {
        ensureHeaderExists(FLASHCARDS_FILE_PATH, FLASHCARDS_HEADER);

        try (BufferedWriter writer = Files.newBufferedWriter(
                FLASHCARDS_FILE_PATH,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(flashcardFile.toFileString());
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Could not write flashcard to file.", e);
        }
    }

    public List<FlashcardFile> getAllFlashcardFiles() {
        ensureHeaderExists(FLASHCARDS_FILE_PATH, FLASHCARDS_HEADER);

        List<FlashcardFile> flashcardFiles = new ArrayList<>();

        try {
            if (!Files.exists(FLASHCARDS_FILE_PATH)) {
                return flashcardFiles;
            }

            List<String> lines = Files.readAllLines(FLASHCARDS_FILE_PATH);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    flashcardFiles.add(FlashcardFile.fromFileString(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read flashcard file.", e);
        }

        return flashcardFiles;
    }

    public void clearFile() {
        writeHeaderOnly(DECKS_FILE_PATH, DECKS_HEADER);
    }

    public void clearFlashcardsFile() {
        writeHeaderOnly(FLASHCARDS_FILE_PATH, FLASHCARDS_HEADER);
    }

    public boolean deleteFlashcard(FlashcardFile flashcard) {
        if (flashcard == null) {
            return false;
        }
    
        ensureHeaderExists(FLASHCARDS_FILE_PATH, FLASHCARDS_HEADER);
    
        try {
            if (!Files.exists(FLASHCARDS_FILE_PATH)) {
                return false;
            }
    
            List<String> lines = Files.readAllLines(FLASHCARDS_FILE_PATH);
            if (lines.isEmpty()) {
                return false;
            }
    
            List<String> updatedLines = new ArrayList<>();
            updatedLines.add(FLASHCARDS_HEADER);
    
            boolean deleted = false;
            String targetLine = flashcard.toFileString();
    
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
    
                if (line.isEmpty()) {
                    continue;
                }
    
                if (!deleted && line.equals(targetLine)) {
                    deleted = true;
                    continue;
                }
    
                updatedLines.add(line);
            }
    
            if (deleted) {
                Files.write(
                        FLASHCARDS_FILE_PATH,
                        updatedLines,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE
                );
            }
    
            return deleted;
    
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
