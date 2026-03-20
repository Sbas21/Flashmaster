package com.flashmaster.data;

import java.util.List;

public class DataTest {
    public static void main(String[] args) {
        DataAccessLayer dal = new DataAccessLayer();

        dal.addDeckFile(new DeckFile(100, "CS 151", "Object oriented concepts"));
        dal.addDeckFile(new DeckFile(101, "CS 154", "Formal Lang"));
        dal.addDeckFile(new DeckFile(102, "English", "Idioms, terms, proverbs in English"));


        List<DeckFile> decks = dal.getAllDeckFiles();

        for (DeckFile deck : decks) {
            System.out.println(deck);
        }
    }
}