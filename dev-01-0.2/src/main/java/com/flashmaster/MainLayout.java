package com.flashmaster;

import com.flashmaster.components.*;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Main layout assembler — composes all components into the full application UI.
 * Mirrors the React Index.tsx page layout.
 *
 * Structure:
 *   VBox (root card)
 *     ├── AppHeader       (gradient title + menu)
 *     ├── HBox (center)
 *     │     ├── AppSidebar (left nav)
 *     │     └── HBox (content)
 *     │           ├── DecksTable   (main table)
 *     │           └── SummaryStats (right panel)
 *     └── SearchBar       (footer)
 */
public class MainLayout extends VBox {

    private HBox centerArea;
    private javafx.scene.Node contentArea;
    private AppSidebar sidebar;

    public MainLayout() {
        getStyleClass().add("main-card");

        // --- Header ---
        AppHeader header = new AppHeader();

        // --- Center Area ---
        sidebar = new AppSidebar(this::handleSidebarSelection);

        DecksTable decksTable = new DecksTable();
        HBox.setHgrow(decksTable, Priority.ALWAYS);

        SummaryStats summaryStats = new SummaryStats();

        // The default "Home" content
        HBox defaultContent = new HBox(24, decksTable, summaryStats);
        defaultContent.setPadding(new Insets(24));
        contentArea = defaultContent;
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        centerArea = new HBox();
        centerArea.getChildren().addAll(sidebar, contentArea);
        VBox.setVgrow(centerArea, Priority.ALWAYS);

        // --- Footer ---
        SearchBar searchBar = new SearchBar();

        // --- Assemble ---
        getChildren().addAll(header, centerArea, searchBar);
    }

    private void handleSidebarSelection(String selection) {
        showContent(selection);
    }

    private void showContent(String selection) {
        // Remove current content
        centerArea.getChildren().remove(contentArea);

        // Create new content based on selection
        javafx.scene.Node newContent;
        switch (selection) {
            case "Manage Decks":
                newContent = new ManageDecksView(this::navigateHome, this::openDefineDeck);
                break;
            case "Define Deck":
                newContent = new DefineDeckView(this::navigateBackToManageDecks);
                break;
            case "Home":
            default:
                DecksTable decksTable = new DecksTable();
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

    private void navigateBackToManageDecks() {
        sidebar.setActiveByLabel("Manage Decks");
        showContent("Manage Decks");
    }
}
