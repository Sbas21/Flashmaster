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

    // CSV location
    private static final String DATA_FOLDER = "data";
    private static final String FILE_NAME = "decks.csv";
    private static final Path FILE_PATH = Paths.get(DATA_FOLDER, FILE_NAME);
    private static final String HEADER = "Deck ID|Deck Name|Description";


    public DataAccessLayer() {
        initializeFile();
    }

    private void initializeFile() {
        try {
            // Create data folder + file if missing
            System.out.println("Creating folder at: " + Paths.get(DATA_FOLDER).toAbsolutePath());
            System.out.println("Creating file at: " + FILE_PATH.toAbsolutePath());

            Files.createDirectories(Paths.get(DATA_FOLDER));

            if (!Files.exists(FILE_PATH)) {
                Files.createFile(FILE_PATH);
                System.out.println("File created.");
                ensureHeaderExists();

            } else {
                System.out.println("File already exists.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize data file.", e);
        }
    }
    private void ensureHeaderExists() {
        try {
            List<String> lines = Files.readAllLines(FILE_PATH);
            // file is empty
            if (lines.isEmpty()) {
                writeHeaderOnly();
                return;
            }

            String firstLine = lines.get(0).trim();
            // header is missing or file is empty
            if (!firstLine.equalsIgnoreCase(HEADER)) {
                List<String> updatedLines = new ArrayList<>();
                updatedLines.add(HEADER);

                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        updatedLines.add(line);
                    }
                }

                Files.write(FILE_PATH, updatedLines, StandardOpenOption.TRUNCATE_EXISTING);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not verify header.", e);
        }
    }
    private void writeHeaderOnly() {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PATH,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            writer.write(HEADER);
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Could not write header.", e);
        }
    }

    public void addDeckFile(DeckFile deckFile) {
        ensureHeaderExists();

        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PATH,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            // Append one row
            writer.write(deckFile.toFileString());
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not write deck to file.", e);
        }
    }

    public List<DeckFile> getAllDeckFiles() {
        ensureHeaderExists();

        List<DeckFile> deckFiles = new ArrayList<>();

        try {
            if (!Files.exists(FILE_PATH)) {
                return deckFiles;
            }

            List<String> lines = Files.readAllLines(FILE_PATH);

            // Skip header row
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (!line.isEmpty()) {
                    deckFiles.add(DeckFile.fromFileString(line));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read deck file.", e);
        }

        return deckFiles;
    }

    public void clearFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PATH,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            writer.write(HEADER);
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Could not clear data file.", e);
        }
    }
}
