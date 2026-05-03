package com.flashmaster.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SummaryStats extends VBox {

    // Static demo stats
    private static final String[][] STATS = {
        {"Total Decks", "2"},
        {"Total Flashcards", "37"},
        {"Mastered Percentage", "45%"},
    };

    public SummaryStats() {
        getStyleClass().add("stats-card");
        setSpacing(16);
        setPadding(new Insets(20));
        setPrefWidth(220);
        setMinWidth(200);

        //  Title
        Label title = new Label("Summary Statistics");
        title.getStyleClass().add("stats-title");

        getChildren().add(title);

        //  Stats
        for (String[] stat : STATS) {
            VBox statItem = new VBox(2);

            Label statLabel = new Label(stat[0]);
            statLabel.getStyleClass().add("stat-label");

            Label statValue = new Label(stat[1]);
            statValue.getStyleClass().add("stat-value");

            statItem.getChildren().addAll(statLabel, statValue);
            getChildren().add(statItem);
        }
    }
}
