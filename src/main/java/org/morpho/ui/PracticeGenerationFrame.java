package org.morpho.ui;

import org.morpho.*;
import org.morpho.level.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Practice screen for word generation.
 * Students enter a root and select a pattern to generate words.
 */
public class PracticeGenerationFrame extends JFrame {
    private Level level;
    private LevelManager manager;
    private JTextField rootField;
    private JComboBox<String> patternCombo;
    private JLabel resultLabel;
    private JTextArea explanationArea;
    private JLabel rootHelperLabel;

    public PracticeGenerationFrame(LevelManager manager, Level level) {
        this.manager = manager;
        this.level = level;

        setTitle("Practice Word Generation - " + level.getName());
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Word Generation Practice", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        inputPanel.add(new JLabel("Enter Root: (3 or 4 letters)"));
        rootField = new JTextField();
        rootField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(rootField);

        rootHelperLabel = new JLabel(" ");
        rootHelperLabel.setForeground(Color.RED);
        inputPanel.add(new JLabel(""));
        inputPanel.add(rootHelperLabel);


        inputPanel.add(new JLabel("Select Pattern:"));
        patternCombo = new JComboBox<>();
        updatePatternComboForRoot();
        rootField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePatternComboForRoot();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePatternComboForRoot();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePatternComboForRoot();
            }
        });
        inputPanel.add(patternCombo);

        JButton generateButton = new JButton("Generate Word");
        JButton randomButton = new JButton("Random Exercise");
        inputPanel.add(generateButton);
        inputPanel.add(randomButton);

        JButton checkButton = new JButton("Show Explanation");
        JButton backButton = new JButton("← Back");
        inputPanel.add(checkButton);
        inputPanel.add(backButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Result panel
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultLabel = new JLabel("Result will appear here", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultPanel.add(resultLabel, BorderLayout.NORTH);

        explanationArea = new JTextArea(5, 30);
        explanationArea.setEditable(false);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(explanationArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(resultPanel, BorderLayout.SOUTH);

        // Button actions
        generateButton.addActionListener(e -> generateWord());
        randomButton.addActionListener(e -> randomExercise());
        checkButton.addActionListener(e -> showExplanation());
        backButton.addActionListener(e -> {
            new StudentDashboardFrame(manager, level);
            dispose();
        });

        add(mainPanel);
        setVisible(true);
    }

    private void generateWord() {
        String root = rootField.getText().trim();
        
        if (root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a root!");
            return;
        }

        if (root.length() != 3 && root.length() != 4) {
            JOptionPane.showMessageDialog(this, "Root must be exactly 3 or 4 letters!");
            return;
        }

        String patternName = (String) patternCombo.getSelectedItem();
        if (patternName == null) {
            JOptionPane.showMessageDialog(this, "Please select a pattern!");
            return;
        }

        String result = level.generateWord(root, patternName);
        if (result != null) {
            resultLabel.setText("Generated Word: " + result);
            explanationArea.setText("");
        } else {
            resultLabel.setText("Error generating word");
        }
    }

    private void randomExercise() {
        List<String> roots = level.getAllRoots();

        if (roots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No roots or patterns available!");
            return;
        }

        // Pick random root and a matching pattern
        String randomRoot = roots.get((int) (Math.random() * roots.size()));
        List<Pattern> patterns = getEnabledPatternsForLength(randomRoot.length());
        if (patterns.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patterns available for this root length!");
            return;
        }
        Pattern randomPattern = patterns.get((int) (Math.random() * patterns.size()));

        rootField.setText(randomRoot);
        updatePatternComboForRoot();
        patternCombo.setSelectedItem(randomPattern.name);
        
        resultLabel.setText("Try to generate the word, then click 'Generate Word' to check!");
        explanationArea.setText("");
    }

    private void showExplanation() {
        String root = rootField.getText().trim();
        String patternName = (String) patternCombo.getSelectedItem();

        if (root.isEmpty() || patternName == null) {
            JOptionPane.showMessageDialog(this, "Generate a word first!");
            return;
        }

        if (root.length() != 3 && root.length() != 4) {
            JOptionPane.showMessageDialog(this, "Root must be exactly 3 or 4 letters!");
            return;
        }

        Pattern pattern = null;
        for (Pattern p : level.getEnabledPatterns()) {
            if (p.name.equals(patternName)) {
                pattern = p;
                break;
            }
        }

        if (pattern == null) return;

        StringBuilder explanation = new StringBuilder();
        explanation.append("Root: ").append(root).append("\n");
        explanation.append("Pattern: ").append(patternName).append("\n");
        explanation.append("Template: ").append(pattern.template).append("\n\n");
        explanation.append("Explanation:\n");
        explanation.append("ف (first letter) → ").append(root.charAt(0)).append("\n");
        explanation.append("ع (second letter) → ").append(root.charAt(1)).append("\n");
        explanation.append("ل (third letter) → ").append(root.charAt(2)).append("\n");
        if (root.length() == 4) {
            explanation.append("ل2 (fourth letter) → ").append(root.charAt(3)).append("\n");
        }

        explanationArea.setText(explanation.toString());
    }

    private List<Pattern> getEnabledPatternsForLength(int length) {
        List<Pattern> filtered = new ArrayList<>();
        for (Pattern p : level.getEnabledPatterns()) {
            int size = p.verifyPattern(p);
            if (size == length) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    private void updatePatternComboForRoot() {
        String root = rootField.getText().trim();
        int length = root.length();
        String currentSelection = (String) patternCombo.getSelectedItem();

        if (length == 3 || length == 4) {
            rootHelperLabel.setText(" ");
        } else {
            rootHelperLabel.setText("Root length must be 3 or 4 letters.");
        }

        patternCombo.removeAllItems();
        if (length != 3 && length != 4) {
            return;
        }

        for (Pattern p : level.getEnabledPatterns()) {
            int size = p.verifyPattern(p);
            if (size != length) {
                continue;
            }
            patternCombo.addItem(p.name);
        }

        if (currentSelection != null) {
            patternCombo.setSelectedItem(currentSelection);
        }
    }
}