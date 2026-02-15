package org.morpho.ui;

import org.morpho.level.*;
import javax.swing.*;
import java.awt.*;

/**
 * Student dashboard - main hub for student activities.
 * Provides access to practice modes, quizzes, and statistics.
 */
public class StudentDashboardFrame extends JFrame {
    private Level level;
    private LevelManager manager;

    public StudentDashboardFrame(LevelManager manager, Level level) {
        this.manager = manager;
        this.level = level;

        setTitle("Student Dashboard - " + level.getName());
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Title
        JLabel titleLabel = new JLabel("Level: " + level.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Activity buttons
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 15, 15));

        JButton practiceGenButton = new JButton("Practice Word Generation");
        JButton practiceValButton = new JButton("Practice Word Validation");
        JButton quizButton = new JButton("Take a Quiz");
        JButton statsButton = new JButton("View My Statistics");
        JButton backButton = new JButton("← Back to Level Selection");

        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        practiceGenButton.setFont(buttonFont);
        practiceValButton.setFont(buttonFont);
        quizButton.setFont(buttonFont);
        statsButton.setFont(buttonFont);
        backButton.setFont(buttonFont);

        practiceGenButton.addActionListener(e -> {
            new PracticeGenerationFrame(manager, level);
            dispose();
        });

        practiceValButton.addActionListener(e -> {
            new PracticeValidationFrame(manager, level);
            dispose();
        });

        quizButton.addActionListener(e -> {
            new QuizFrame(manager, level);
            dispose();
        });

        statsButton.addActionListener(e -> {
            showStatistics();
        });

        backButton.addActionListener(e -> {
            new LevelSelectionFrame(manager);
            dispose();
        });

        buttonPanel.add(practiceGenButton);
        buttonPanel.add(practiceValButton);
        buttonPanel.add(quizButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private void showStatistics() {
        Statistics stats = level.getStats();
        
        String message = String.format(
            "Session Statistics for %s:\n\n" +
            "Total Questions: %d\n" +
            "Correct Answers: %d\n" +
            "Wrong Answers: %d\n" +
            "Accuracy: %.1f%%\n\n" +
            "Best Pattern: %s\n" +
            "Most Difficult Pattern: %s",
            level.getName(),
            stats.getTotalQuestions(),
            stats.getCorrectAnswers(),
            stats.getWrongAnswers(),
            stats.getAccuracy(),
            stats.getBestPattern(),
            stats.getMostDifficultPattern()
        );

        JOptionPane.showMessageDialog(this, message, 
            "Your Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
}