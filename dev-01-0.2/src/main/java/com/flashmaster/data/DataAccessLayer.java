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

    private static final String DATA_FOLDER = "dev-01-0.2/src/main/data";
    private static final String FILE_NAME = "decks.csv";
    private static final Path FILE_PATH = Paths.get(DATA_FOLDER, FILE_NAME);

    public DataAccessLayer() {
        initializeFile();
    }

    private void initializeFile() {
        try {
            System.out.println("Creating folder at: " + Paths.get(DATA_FOLDER).toAbsolutePath());
            System.out.println("Creating file at: " + FILE_PATH.toAbsolutePath());

            Files.createDirectories(Paths.get(DATA_FOLDER));

            if (!Files.exists(FILE_PATH)) {
                Files.createFile(FILE_PATH);

                // Write header
                try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                    writer.write("Unique ID|Deck name|Description");
                    writer.newLine();
                }

                System.out.println("File created.");
            } else {
                System.out.println("File already exists.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize data file.", e);
        }
    }

    public void addDeckFile(DeckFile deckFile) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PATH,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(deckFile.toFileString());
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not write deck to file.", e);
        }
    }

    public List<DeckFile> getAllDeckFiles() {
        List<DeckFile> deckFiles = new ArrayList<>();

        try {
            if (!Files.exists(FILE_PATH)) {
                return deckFiles;
            }

            List<String> lines = Files.readAllLines(FILE_PATH);

            // Skip header (index 0)
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

            writer.write("Unique ID|Deck name|Description");
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Could not clear data file.", e);
        }
    }
}