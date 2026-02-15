package org.morpho.ui;

import org.morpho.*;
import org.morpho.level.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Frame for managing patterns within a selected level.
 * Teachers can enable/disable patterns and add new patterns.
 */
public class ManagePatternsFrame extends JFrame {
    private LevelManager manager;
    private Level currentLevel;
    private JComboBox<String> levelCombo;
    private JPanel patternPanel;

    public ManagePatternsFrame(LevelManager manager) {
        this.manager = manager;

        setTitle("Manage Patterns");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and level selector
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JLabel titleLabel = new JLabel("Pattern Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel);

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        selectorPanel.add(new JLabel("Select Level:"));
        
        levelCombo = new JComboBox<>();
        for (String levelName : manager.listLevels()) {
            levelCombo.addItem(levelName);
        }
        levelCombo.addActionListener(e -> selectLevel());
        selectorPanel.add(levelCombo);
        topPanel.add(selectorPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Pattern panel with checkboxes
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createTitledBorder("Patterns (Check to Enable)"));
        patternPanel = new JPanel();
        patternPanel.setLayout(new BoxLayout(patternPanel, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(patternPanel);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        JButton addButton = new JButton("Add New Pattern");
        JButton removeButton = new JButton("Remove Pattern");
        JButton enableAllButton = new JButton("Enable All");
        JButton disableAllButton = new JButton("Disable All");
        JButton backButton = new JButton("← Back");

        addButton.setFont(new Font("Arial", Font.PLAIN, 13));
        removeButton.setFont(new Font("Arial", Font.PLAIN, 13));
        enableAllButton.setFont(new Font("Arial", Font.PLAIN, 13));
        disableAllButton.setFont(new Font("Arial", Font.PLAIN, 13));
        backButton.setFont(new Font("Arial", Font.PLAIN, 13));

        addButton.addActionListener(e -> addPattern());
        removeButton.addActionListener(e -> removePattern());
        enableAllButton.addActionListener(e -> enableAll());
        disableAllButton.addActionListener(e -> disableAll());
        backButton.addActionListener(e -> {
            new TeacherDashboardFrame(manager);
            dispose();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(enableAllButton);
        buttonPanel.add(disableAllButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        add(mainPanel);
        
        // Select first level by default
        if (levelCombo.getItemCount() > 0) {
            selectLevel();
        }
        
        setVisible(true);
    }

    private void selectLevel() {
        String levelName = (String) levelCombo.getSelectedItem();
        if (levelName != null) {
            currentLevel = manager.getLevel(levelName);
            refreshPatternList();
        }
    }

    private void refreshPatternList() {
        patternPanel.removeAll();
        
        if (currentLevel != null) {
            List<Pattern> patterns = currentLevel.getAllPatterns();
            
            if (patterns.isEmpty()) {
                JLabel emptyLabel = new JLabel("No patterns in this level");
                emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                patternPanel.add(emptyLabel);
            } else {
                for (Pattern pattern : patterns) {
                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    
                    JCheckBox checkbox = new JCheckBox(pattern.name);
                    checkbox.setFont(new Font("Arial", Font.PLAIN, 14));
                    checkbox.setSelected(currentLevel.isPatternEnabled(pattern.name));
                    
                    checkbox.addActionListener(e -> {
                        if (checkbox.isSelected()) {
                            currentLevel.enablePattern(pattern.name);
                        } else {
                            currentLevel.disablePattern(pattern.name);
                        }
                    });
                    
                    rowPanel.add(checkbox);
                    
                    // Show template
                    JLabel templateLabel = new JLabel("Template: " + pattern.template);
                    templateLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
                    templateLabel.setForeground(Color.GRAY);
                    rowPanel.add(templateLabel);
                    
                    patternPanel.add(rowPanel);
                }
            }
        }
        
        patternPanel.revalidate();
        patternPanel.repaint();
    }

    private void addPattern() {
        if (currentLevel == null) {
            JOptionPane.showMessageDialog(this, "Please select a level first!");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        
        JTextField nameField = new JTextField();
        JTextField templateField = new JTextField();

        panel.add(new JLabel("Pattern Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Template (space-separated):"));
        panel.add(templateField);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Add New Pattern", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String templateStr = templateField.getText().trim();

            if (name.isEmpty() || templateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            // Check if pattern already exists
            for (Pattern p : currentLevel.getAllPatterns()) {
                if (p.name.equals(name)) {
                    JOptionPane.showMessageDialog(this,
                        "Pattern '" + name + "' already exists!",
                        "Duplicate Pattern", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            List<String> template = Arrays.asList(templateStr.split(" "));
            currentLevel.addPattern(name, template);
            refreshPatternList();
            
            JOptionPane.showMessageDialog(this,
                "Pattern '" + name + "' added successfully and enabled!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removePattern() {
        if (currentLevel == null) {
            JOptionPane.showMessageDialog(this, "Please select a level first!");
            return;
        }

        List<Pattern> patterns = currentLevel.getAllPatterns();
        if (patterns.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patterns to remove!");
            return;
        }

        String[] patternNames = new String[patterns.size()];
        for (int i = 0; i < patterns.size(); i++) {
            patternNames[i] = patterns.get(i).name;
        }

        String selected = (String) JOptionPane.showInputDialog(this,
            "Select pattern to remove:",
            "Remove Pattern",
            JOptionPane.QUESTION_MESSAGE,
            null,
            patternNames,
            patternNames[0]);

        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove pattern '" + selected + "'?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                currentLevel.removePattern(selected);
                refreshPatternList();
                JOptionPane.showMessageDialog(this, "Pattern removed successfully!");
            }
        }
    }

    private void enableAll() {
        if (currentLevel == null) return;
        
        for (Pattern pattern : currentLevel.getAllPatterns()) {
            currentLevel.enablePattern(pattern.name);
        }
        refreshPatternList();
        JOptionPane.showMessageDialog(this, "All patterns enabled!");
    }

    private void disableAll() {
        if (currentLevel == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to disable all patterns?\n" +
            "Students won't be able to use any patterns in this level.",
            "Confirm Disable All",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            for (Pattern pattern : currentLevel.getAllPatterns()) {
                currentLevel.disablePattern(pattern.name);
            }
            refreshPatternList();
            JOptionPane.showMessageDialog(this, "All patterns disabled!");
        }
    }
}