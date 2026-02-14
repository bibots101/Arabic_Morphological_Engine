package org.morpho.ui;

import org.morpho.level.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Practice screen for word validation.
 * Students enter a word and root to check if the word is derived from that root.
 */
public class PracticeValidationFrame extends JFrame {
    private Level level;
    private LevelManager manager;
    private JTextField wordField;
    private JTextField rootField;
    private JLabel resultLabel;
    private JTextArea feedbackArea;

    public PracticeValidationFrame(LevelManager manager, Level level) {
        this.manager = manager;
        this.level = level;

        setTitle("Practice Word Validation - " + level.getName());
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Word Validation Practice", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        inputPanel.add(new JLabel("Enter Word to Validate:"));
        wordField = new JTextField();
        wordField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(wordField);

        inputPanel.add(new JLabel("Enter Root (3 letters):"));
        rootField = new JTextField();
        rootField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(rootField);

        JButton validateButton = new JButton("Validate Word");
        JButton randomButton = new JButton("Random Exercise");
        inputPanel.add(validateButton);
        inputPanel.add(randomButton);

        JButton clearButton = new JButton("Clear");
        JButton backButton = new JButton("← Back");
        inputPanel.add(clearButton);
        inputPanel.add(backButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Result panel
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultLabel = new JLabel("Result will appear here", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultPanel.add(resultLabel, BorderLayout.NORTH);

        feedbackArea = new JTextArea(5, 30);
        feedbackArea.setEditable(false);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(resultPanel, BorderLayout.SOUTH);

        // Button actions
        validateButton.addActionListener(e -> validateWord());
        randomButton.addActionListener(e -> randomExercise());
        clearButton.addActionListener(e -> clearFields());
        backButton.addActionListener(e -> {
            new StudentDashboardFrame(manager, level);
            dispose();
        });

        add(mainPanel);
        setVisible(true);
    }

    private void validateWord() {
        String word = wordField.getText().trim();
        String root = rootField.getText().trim();

        if (word.isEmpty() || root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both word and root!");
            return;
        }

        if (root.length() != 3) {
            JOptionPane.showMessageDialog(this, "Root must be exactly 3 letters!");
            return;
        }

        String patternName = level.validateWord(word, root);
        
        if (patternName != null) {
            resultLabel.setText("✓ VALID");
            resultLabel.setForeground(new Color(0, 150, 0));
            
            feedbackArea.setText(
                "Success!\n\n" +
                "The word '" + word + "' is correctly derived from the root '" + root + "'\n" +
                "Pattern used: " + patternName + "\n\n" +
                "This pattern is " + (level.isPatternEnabled(patternName) ? "enabled" : "disabled") + 
                " in this level."
            );
            
            // Record statistics
            level.getStats().recordAnswer(true, patternName);
        } else {
            resultLabel.setText("✗ INVALID");
            resultLabel.setForeground(Color.RED);
            
            feedbackArea.setText(
                "Incorrect!\n\n" +
                "The word '" + word + "' is NOT derived from the root '" + root + "'\n" +
                "Or it uses a pattern that doesn't exist in the system.\n\n" +
                "Try again with a different word or root."
            );
            
            // Record as mistake (use "unknown" as pattern)
            level.getStats().recordAnswer(false, "unknown");
        }
    }

    private void randomExercise() {
        List<String> roots = level.getAllRoots();
        
        if (roots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No roots available!");
            return;
        }

        List<org.morpho.Pattern> patterns = level.getEnabledPatterns();
        if (patterns.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patterns enabled!");
            return;
        }

        // Generate a valid word for the student to validate
        String randomRoot = roots.get((int) (Math.random() * roots.size()));
        org.morpho.Pattern randomPattern = patterns.get((int) (Math.random() * patterns.size()));
        
        String generatedWord = level.generateWord(randomRoot, randomPattern.name);
        
        if (generatedWord != null) {
            wordField.setText(generatedWord);
            rootField.setText("");
            resultLabel.setText("Can you identify the root of this word?");
            resultLabel.setForeground(Color.BLACK);
            feedbackArea.setText("Hint: This word was generated using the pattern: " + randomPattern.name);
        }
    }

    private void clearFields() {
        wordField.setText("");
        rootField.setText("");
        resultLabel.setText("Result will appear here");
        resultLabel.setForeground(Color.BLACK);
        feedbackArea.setText("");
    }
}