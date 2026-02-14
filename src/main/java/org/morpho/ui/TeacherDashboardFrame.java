package org.morpho.ui;

import org.morpho.level.*;
import javax.swing.*;
import java.awt.*;

/**
 * Teacher dashboard - main control panel for curriculum management.
 * Provides access to level, root, and pattern management.
 */
public class TeacherDashboardFrame extends JFrame {
    private LevelManager manager;

    public TeacherDashboardFrame(LevelManager manager) {
        this.manager = manager;

        setTitle("Teacher Dashboard");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Title
        JLabel titleLabel = new JLabel("Teacher Control Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 15, 15));

        JButton manageLevelsButton = new JButton("Manage Levels");
        JButton manageRootsButton = new JButton("Manage Roots");
        JButton managePatternsButton = new JButton("Manage Patterns");
        JButton viewStatsButton = new JButton("View All Statistics");
        JButton backButton = new JButton("← Back to Main Menu");

        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        manageLevelsButton.setFont(buttonFont);
        manageRootsButton.setFont(buttonFont);
        managePatternsButton.setFont(buttonFont);
        viewStatsButton.setFont(buttonFont);
        backButton.setFont(buttonFont);

        manageLevelsButton.addActionListener(e -> {
            new ManageLevelsFrame(manager);
            dispose();
        });

        manageRootsButton.addActionListener(e -> {
            if (manager.getLevelCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please create a level first!",
                    "No Levels", JOptionPane.WARNING_MESSAGE);
            } else {
                new ManageRootsFrame(manager);
                dispose();
            }
        });

        managePatternsButton.addActionListener(e -> {
            if (manager.getLevelCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Please create a level first!",
                    "No Levels", JOptionPane.WARNING_MESSAGE);
            } else {
                new ManagePatternsFrame(manager);
                dispose();
            }
        });

        viewStatsButton.addActionListener(e -> showAllStatistics());

        backButton.addActionListener(e -> {
            new MainMenuFrame(manager);
            dispose();
        });

        buttonPanel.add(manageLevelsButton);
        buttonPanel.add(manageRootsButton);
        buttonPanel.add(managePatternsButton);
        buttonPanel.add(viewStatsButton);
        buttonPanel.add(new JLabel("")); // Spacer
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Current Levels: " + manager.getLevelCount());
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoPanel.add(infoLabel);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void showAllStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statistics Summary\n");
        sb.append("===================\n\n");

        for (String levelName : manager.listLevels()) {
            Level level = manager.getLevel(levelName);
            Statistics stats = level.getStats();
            
            sb.append(levelName).append(":\n");
            sb.append("  Questions: ").append(stats.getTotalQuestions()).append("\n");
            sb.append("  Correct: ").append(stats.getCorrectAnswers()).append("\n");
            sb.append("  Accuracy: ").append(String.format("%.1f%%", stats.getAccuracy())).append("\n");
            sb.append("  Most Difficult: ").append(stats.getMostDifficultPattern()).append("\n\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
            "All Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
}