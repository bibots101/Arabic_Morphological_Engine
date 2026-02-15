package org.morpho.ui;

import org.morpho.level.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Frame for managing curriculum levels.
 * Allows teachers to create, view, and delete levels.
 */
public class ManageLevelsFrame extends JFrame {
    private LevelManager manager;
    private JList<String> levelList;
    private DefaultListModel<String> listModel;

    public ManageLevelsFrame(LevelManager manager) {
        this.manager = manager;

        setTitle("Manage Levels");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Level Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Level list panel
        JPanel listPanel = new JPanel(new BorderLayout(10, 10));
        listPanel.setBorder(BorderFactory.createTitledBorder("Existing Levels"));

        listModel = new DefaultListModel<>();
        refreshLevelList();
        
        levelList = new JList<>(listModel);
        levelList.setFont(new Font("Arial", Font.PLAIN, 14));
        levelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(levelList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(listPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton createButton = new JButton("Create New Level");
        JButton deleteButton = new JButton("Delete Selected Level");
        JButton viewButton = new JButton("View Level Details");
        JButton backButton = new JButton("← Back");

        createButton.setFont(new Font("Arial", Font.PLAIN, 13));
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 13));
        viewButton.setFont(new Font("Arial", Font.PLAIN, 13));
        backButton.setFont(new Font("Arial", Font.PLAIN, 13));

        createButton.addActionListener(e -> createLevel());
        deleteButton.addActionListener(e -> deleteLevel());
        viewButton.addActionListener(e -> viewLevelDetails());
        backButton.addActionListener(e -> {
            new TeacherDashboardFrame(manager);
            dispose();
        });

        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);
    }

    private void refreshLevelList() {
        listModel.clear();
        for (String levelName : manager.listLevels()) {
            listModel.addElement(levelName);
        }
    }

    private void createLevel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JTextField nameField = new JTextField();
        JTextField rootsField = new JTextField("src/main/data/");
        JTextField patternsField = new JTextField("src/main/data/");

        panel.add(new JLabel("Level Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Roots File Path:"));
        panel.add(rootsField);
        panel.add(new JLabel("Patterns File Path:"));
        panel.add(patternsField);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Create New Level", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String rootsPath = rootsField.getText().trim();
            String patternsPath = patternsField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Level name cannot be empty!");
                return;
            }

            if (manager.hasLevel(name)) {
                JOptionPane.showMessageDialog(this, "Level already exists!");
                return;
            }

            // If user entered just the level name, create the directory structure
            if (!rootsPath.contains(".txt") || !patternsPath.contains(".txt")) {
                String basePath = "src/main/data/" + name + "/";
                rootsPath = basePath + "roots.txt";
                patternsPath = basePath + "patterns.txt";

                // Create directories
                File levelDir = new File("src/main/data/" + name);
                if (!levelDir.exists()) {
                    levelDir.mkdirs();
                }

                // Create empty files
                try {
                    new File(rootsPath).createNewFile();
                    new File(patternsPath).createNewFile();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error creating files: " + ex.getMessage());
                    return;
                }
            }

            manager.addLevel(name, rootsPath, patternsPath);
            refreshLevelList();
            
            JOptionPane.showMessageDialog(this,
                "Level '" + name + "' created successfully!\n\n" +
                "You can now add roots and patterns to this level.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteLevel() {
        String selected = levelList.getSelectedValue();
        
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a level to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete level '" + selected + "'?\n" +
            "This action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            manager.removeLevel(selected);
            refreshLevelList();
            JOptionPane.showMessageDialog(this, "Level deleted successfully!");
        }
    }

    private void viewLevelDetails() {
        String selected = levelList.getSelectedValue();
        
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a level!");
            return;
        }

        Level level = manager.getLevel(selected);
        
        String details = String.format(
            "Level: %s\n\n" +
            "Total Roots: %d\n" +
            "Total Patterns: %d\n" +
            "Enabled Patterns: %d\n\n" +
            "Statistics (Current Session):\n" +
            "  Questions Answered: %d\n" +
            "  Accuracy: %.1f%%\n" +
            "  Most Difficult Pattern: %s",
            level.getName(),
            level.getAllRoots().size(),
            level.getAllPatterns().size(),
            level.getEnabledPatterns().size(),
            level.getStats().getTotalQuestions(),
            level.getStats().getAccuracy(),
            level.getStats().getMostDifficultPattern()
        );

        JOptionPane.showMessageDialog(this, details,
            "Level Details", JOptionPane.INFORMATION_MESSAGE);
    }
}