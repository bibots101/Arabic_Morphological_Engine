package org.morpho.ui;

import org.morpho.*;
import org.morpho.level.*;
import javax.swing.*;
import java.awt.*;
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
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        inputPanel.add(new JLabel("Enter Root (3 letters):"));
        rootField = new JTextField();
        rootField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(rootField);

        inputPanel.add(new JLabel("Select Pattern:"));
        patternCombo = new JComboBox<>();
        
        // Populate with enabled patterns only
        List<Pattern> enabledPatterns = level.getEnabledPatterns();
        for (Pattern p : enabledPatterns) {
            patternCombo.addItem(p.name);
        }
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

        if (root.length() != 3) {
            JOptionPane.showMessageDialog(this, "Root must be exactly 3 letters!");
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
        List<Pattern> patterns = level.getEnabledPatterns();

        if (roots.isEmpty() || patterns.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No roots or patterns available!");
            return;
        }

        // Pick random root and pattern
        String randomRoot = roots.get((int) (Math.random() * roots.size()));
        Pattern randomPattern = patterns.get((int) (Math.random() * patterns.size()));

        rootField.setText(randomRoot);
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

        explanationArea.setText(explanation.toString());
    }
}