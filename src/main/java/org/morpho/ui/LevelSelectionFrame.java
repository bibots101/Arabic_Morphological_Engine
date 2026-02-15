package org.morpho.ui;

import org.morpho.level.*;
import javax.swing.*;
import java.awt.*;

/**
 * Level selection screen for students.
 * Displays all available levels as buttons.
 */
public class LevelSelectionFrame extends JFrame {

    public LevelSelectionFrame(LevelManager manager) {
        setTitle("Select Learning Level");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Choose Your Level", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Level buttons panel
        JPanel levelPanel = new JPanel();
        levelPanel.setLayout(new GridLayout(manager.listLevels().size() + 1, 1, 10, 10));

        for (String levelName : manager.listLevels()) {
            JButton levelButton = new JButton(levelName);
            levelButton.setFont(new Font("Arial", Font.PLAIN, 14));
            
            levelButton.addActionListener(e -> {
                Level level = manager.getLevel(levelName);
                new StudentDashboardFrame(manager, level);
                dispose();
            });

            levelPanel.add(levelButton);
        }

        // Back button
        JButton backButton = new JButton("← Back to Main Menu");
        backButton.addActionListener(e -> {
            new MainMenuFrame(manager);
            dispose();
        });
        levelPanel.add(backButton);

        mainPanel.add(levelPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }
}