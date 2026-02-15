package org.morpho.level;

import java.util.*;

/**
 * Tracks student performance statistics for a single session.
 * Data is not persisted and resets when the application closes.
 */
public class Statistics {
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private Map<String, Integer> patternMistakes;
    private Map<String, Integer> patternSuccesses;

    public Statistics() {
        this.totalQuestions = 0;
        this.correctAnswers = 0;
        this.wrongAnswers = 0;
        this.patternMistakes = new HashMap<>();
        this.patternSuccesses = new HashMap<>();
    }

    public void recordAnswer(boolean correct, String patternName) {
        totalQuestions++;
        
        if (correct) {
            correctAnswers++;
            patternSuccesses.put(patternName, 
                patternSuccesses.getOrDefault(patternName, 0) + 1);
        } else {
            wrongAnswers++;
            patternMistakes.put(patternName, 
                patternMistakes.getOrDefault(patternName, 0) + 1);
        }
    }

    public double getAccuracy() {
        if (totalQuestions == 0) return 0.0;
        return (double) correctAnswers / totalQuestions * 100.0;
    }

    public String getMostDifficultPattern() {
        if (patternMistakes.isEmpty()) return "None";
        
        return patternMistakes.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
    }

    public String getBestPattern() {
        if (patternSuccesses.isEmpty()) return "None";
        
        return patternSuccesses.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
    }

    public void reset() {
        totalQuestions = 0;
        correctAnswers = 0;
        wrongAnswers = 0;
        patternMistakes.clear();
        patternSuccesses.clear();
    }

    // Getters
    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public Map<String, Integer> getPatternMistakes() {
        return new HashMap<>(patternMistakes);
    }

    public Map<String, Integer> getPatternSuccesses() {
        return new HashMap<>(patternSuccesses);
    }
}