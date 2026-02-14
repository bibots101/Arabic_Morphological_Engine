package org.morpho.ui;

import org.morpho.*;
import org.morpho.level.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Quiz frame for student assessment.
 * Provides a timed or untimed quiz with multiple questions.
 */
public class QuizFrame extends JFrame {
    private Level level;
    private LevelManager manager;
    private int currentQuestion = 0;
    private int totalQuestions;
    private int score = 0;
    
    private List<QuizQuestion> questions;
    
    private JLabel questionLabel;
    private JTextField answerField;
    private JLabel progressLabel;
    private JButton submitButton;
    private JButton nextButton;

    public QuizFrame(LevelManager manager, Level level) {
        this.manager = manager;
        this.level = level;
        
        // Ask user for quiz configuration
        String input = JOptionPane.showInputDialog(this, 
            "How many questions would you like? (5-20)", "10");
        
        try {
            totalQuestions = Integer.parseInt(input);
            if (totalQuestions < 5) totalQuestions = 5;
            if (totalQuestions > 20) totalQuestions = 20;
        } catch (Exception e) {
            totalQuestions = 10;
        }

        setTitle("Quiz - " + level.getName());
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Generate questions
        generateQuestions();

        // Setup UI
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Progress label
        progressLabel = new JLabel();
        progressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        updateProgress();
        mainPanel.add(progressLabel, BorderLayout.NORTH);

        // Question panel
        JPanel questionPanel = new JPanel(new BorderLayout(10, 10));
        questionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionPanel.add(questionLabel, BorderLayout.NORTH);

        answerField = new JTextField();
        answerField.setFont(new Font("Arial", Font.PLAIN, 18));
        questionPanel.add(answerField, BorderLayout.CENTER);

        mainPanel.add(questionPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        submitButton = new JButton("Submit Answer");
        nextButton = new JButton("Next Question →");
        JButton quitButton = new JButton("Quit Quiz");

        submitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        nextButton.setFont(new Font("Arial", Font.PLAIN, 14));
        quitButton.setFont(new Font("Arial", Font.PLAIN, 14));

        nextButton.setEnabled(false);

        submitButton.addActionListener(e -> submitAnswer());
        nextButton.addActionListener(e -> nextQuestion());
        quitButton.addActionListener(e -> quitQuiz());

        buttonPanel.add(submitButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(quitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        displayCurrentQuestion();
        setVisible(true);
    }

    private void generateQuestions() {
        questions = new ArrayList<>();
        List<String> roots = level.getAllRoots();
        List<Pattern> patterns = level.getEnabledPatterns();

        if (roots.isEmpty() || patterns.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No roots or patterns available for quiz!");
            return;
        }

        Random rand = new Random();

        for (int i = 0; i < totalQuestions; i++) {
            // Randomly choose between generation and validation questions
            boolean isGeneration = rand.nextBoolean();
            
            String root = roots.get(rand.nextInt(roots.size()));
            Pattern pattern = patterns.get(rand.nextInt(patterns.size()));

            if (isGeneration) {
                // Generation question: "Generate word from root X using pattern Y"
                String correctAnswer = level.generateWord(root, pattern.name);
                questions.add(new QuizQuestion(
                    "Generate a word from root '" + root + "' using pattern '" + pattern.name + "'",
                    correctAnswer,
                    pattern.name
                ));
            } else {
                // Validation question: "What is the root of word X?"
                String word = level.generateWord(root, pattern.name);
                questions.add(new QuizQuestion(
                    "What is the root of the word '" + word + "'?",
                    root,
                    pattern.name
                ));
            }
        }
    }

    private void displayCurrentQuestion() {
        if (currentQuestion < questions.size()) {
            QuizQuestion q = questions.get(currentQuestion);
            questionLabel.setText("<html>" + q.question + "</html>");
            answerField.setText("");
            answerField.setEnabled(true);
            submitButton.setEnabled(true);
            nextButton.setEnabled(false);
        }
    }

    private void updateProgress() {
        progressLabel.setText(String.format("Question %d of %d | Score: %d/%d", 
            currentQuestion + 1, totalQuestions, score, currentQuestion));
    }

    private void submitAnswer() {
        QuizQuestion q = questions.get(currentQuestion);
        String userAnswer = answerField.getText().trim();

        if (userAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an answer!");
            return;
        }

        boolean correct = userAnswer.equals(q.correctAnswer);
        
        if (correct) {
            score++;
            JOptionPane.showMessageDialog(this, 
                "✓ Correct!\n\nYour answer: " + userAnswer,
                "Correct", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "✗ Incorrect!\n\nYour answer: " + userAnswer + 
                "\nCorrect answer: " + q.correctAnswer,
                "Incorrect", JOptionPane.ERROR_MESSAGE);
        }

        // Record in statistics
        level.getStats().recordAnswer(correct, q.patternName);

        answerField.setEnabled(false);
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
        updateProgress();
    }

    private void nextQuestion() {
        currentQuestion++;
        
        if (currentQuestion >= totalQuestions) {
            showResults();
        } else {
            displayCurrentQuestion();
            updateProgress();
        }
    }

    private void showResults() {
        double percentage = (double) score / totalQuestions * 100;
        
        String message = String.format(
            "Quiz Complete!\n\n" +
            "Score: %d / %d\n" +
            "Percentage: %.1f%%\n\n" +
            "Great job practicing!",
            score, totalQuestions, percentage
        );

        JOptionPane.showMessageDialog(this, message, 
            "Quiz Results", JOptionPane.INFORMATION_MESSAGE);

        new StudentDashboardFrame(manager, level);
        dispose();
    }

    private void quitQuiz() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to quit the quiz?\nYour progress will be lost.",
            "Quit Quiz",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            new StudentDashboardFrame(manager, level);
            dispose();
        }
    }

    // Inner class to represent a quiz question
    private static class QuizQuestion {
        String question;
        String correctAnswer;
        String patternName;

        QuizQuestion(String question, String correctAnswer, String patternName) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.patternName = patternName;
        }
    }
}