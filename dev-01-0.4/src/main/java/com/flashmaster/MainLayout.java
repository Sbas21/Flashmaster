package com.flashmaster;

import com.flashmaster.components.*;
import com.flashmaster.data.DataAccessLayer;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainLayout extends VBox {

    private HBox centerArea;
    private javafx.scene.Node contentArea;
    private AppSidebar sidebar;
    private final DataAccessLayer dataAccessLayer;

    public MainLayout() {
        getStyleClass().add("main-card");
        // Shared data layer
        dataAccessLayer = new DataAccessLayer();

        // Top header
        AppHeader header = new AppHeader();

        // Left navigation
        sidebar = new AppSidebar(this::handleSidebarSelection);

        // Home content
        DecksTable decksTable = new DecksTable("All Decks");
        decksTable.setDecks(dataAccessLayer.getAllDeckFiles());
        HBox.setHgrow(decksTable, Priority.ALWAYS);

        SummaryStats summaryStats = new SummaryStats();

        HBox defaultContent = new HBox(24, decksTable, summaryStats);
        defaultContent.setPadding(new Insets(24));
        contentArea = defaultContent;
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        centerArea = new HBox();
        centerArea.getChildren().addAll(sidebar, contentArea);
        VBox.setVgrow(centerArea, Priority.ALWAYS);

        // Bottom search bar
        SearchBar searchBar = new SearchBar();

        getChildren().addAll(header, centerArea, searchBar);
    }

    private void handleSidebarSelection(String selection) {
        showContent(selection);
    }

    private void showContent(String selection) {
        // Swap the center content
        centerArea.getChildren().remove(contentArea);

        javafx.scene.Node newContent;
        switch (selection) {
            case "Manage Decks":
                newContent = new ManageDecksView(this::navigateHome, this::openDefineDeck);
                break;
            case "Manage Flashcards":
                newContent = new ManageFlashcardsView(this::navigateHome, this::openDefineFlashcards);
                break;
            case "Define Deck":
                newContent = new DefineDeckView(dataAccessLayer, this::navigateBackToManageDecks);
                break;
            case "Define Flashcards":
                newContent = new DefineFlashcardsView(dataAccessLayer, this::navigateBackToManageFlashcards);
                break;
            case "List Decks":
                newContent = new ListDecksView(dataAccessLayer, this::navigateBackToManageDecks);
                break;
            case "Home":
            default:
                DecksTable decksTable = new DecksTable("All Decks");
                decksTable.setDecks(dataAccessLayer.getAllDeckFiles());
                HBox.setHgrow(decksTable, Priority.ALWAYS);
                SummaryStats summaryStats = new SummaryStats();
                newContent = new HBox(24, decksTable, summaryStats);
                ((HBox) newContent).setPadding(new Insets(24));
                break;
        }

        contentArea = newContent;
        HBox.setHgrow(contentArea, Priority.ALWAYS);
        centerArea.getChildren().add(contentArea);
    }

    private void navigateHome() {
        sidebar.setActiveByLabel("Home");
        showContent("Home");
    }

    private void openDefineDeck() {
        showContent("Define Deck");
    }

    private void openDefineFlashcards() {
        showContent("Define Flashcards");
    }

    private void navigateBackToManageDecks() {
        sidebar.setActiveByLabel("Manage Decks");
        showContent("Manage Decks");
    }

    private void navigateBackToManageFlashcards() {
        sidebar.setActiveByLabel("Manage Flashcards");
        showContent("Manage Flashcards");
    }
}
