package org.morpho.ui;

import org.morpho.level.LevelManager;
import javax.swing.*;
import java.awt.*;

/**
 * Main menu frame - entry point of the pedagogical interface.
 * Allows users to choose between Teacher Mode and Student Mode.
 */
public class MainMenuFrame extends JFrame {

    public MainMenuFrame(LevelManager manager) {
        setTitle("Arabic Morphological Learning System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title label
        JLabel titleLabel = new JLabel("Arabic Morphological Engine", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 20, 20));

        JButton studentButton = new JButton("Student Mode");
        JButton teacherButton = new JButton("Teacher Mode");

        // Style buttons
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        studentButton.setFont(buttonFont);
        teacherButton.setFont(buttonFont);

        studentButton.addActionListener(e -> {
            if (manager.getLevelCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No levels available. Please ask your teacher to create levels first.",
                    "No Levels",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                new LevelSelectionFrame(manager);
                dispose();
            }
        });

        teacherButton.addActionListener(e -> {
            new TeacherDashboardFrame(manager);
            dispose();
        });

        buttonPanel.add(studentButton);
        buttonPanel.add(teacherButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }
}