package com.bomberman.game;

import java.util.*;
import java.io.*;

public class ScoreManager {
    private List<ScoreEntry> scores;
    private static final String FICHIER_SCORES = "scores.txt";

    public ScoreManager() {
        scores = new ArrayList<>();
        chargerScores();
    }

    public void ajouterScore(int score, String nom) {
        scores.add(new ScoreEntry(nom, score));
        scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        // Garder seulement les 10 meilleurs scores
        if (scores.size() > 10) {
            scores = scores.subList(0, 10);
        }

        sauvegarderScores();
    }

    private void chargerScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FICHIER_SCORES))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parts = ligne.split(",");
                if (parts.length == 2) {
                    scores.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1])));
                }
            }
        } catch (IOException e) {
            // Fichier n'existe pas encore, c'est normal
        }
    }

    private void sauvegarderScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FICHIER_SCORES))) {
            for (ScoreEntry score : scores) {
                writer.println(score.getNom() + "," + score.getScore());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ScoreEntry> getTopScores() {
        return new ArrayList<>(scores);
    }

    public static class ScoreEntry {
        private String nom;
        private int score;

        public ScoreEntry(String nom, int score) {
            this.nom = nom;
            this.score = score;
        }

        public String getNom() { return nom; }
        public int getScore() { return score; }
    }
}
