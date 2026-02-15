package org.morpho.ui;

import org.morpho.level.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Frame for managing roots within a selected level.
 * Teachers can add, remove, and view roots.
 */
public class ManageRootsFrame extends JFrame {
    private LevelManager manager;
    private Level currentLevel;
    private JList<String> rootList;
    private DefaultListModel<String> listModel;
    private JComboBox<String> levelCombo;

    public ManageRootsFrame(LevelManager manager) {
        this.manager = manager;

        setTitle("Manage Roots");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and level selector
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JLabel titleLabel = new JLabel("Root Management", SwingConstants.CENTER);
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

        // Root list panel
        JPanel listPanel = new JPanel(new BorderLayout(10, 10));
        listPanel.setBorder(BorderFactory.createTitledBorder("Roots in Selected Level"));

        listModel = new DefaultListModel<>();
        rootList = new JList<>(listModel);
        rootList.setFont(new Font("Arial", Font.PLAIN, 14));
        rootList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(rootList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(listPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        JButton addButton = new JButton("Add Root");
        JButton removeButton = new JButton("Remove Selected Root");
        JButton viewButton = new JButton("View Root Details");
        JButton refreshButton = new JButton("↻ Refresh List");
        JButton backButton = new JButton("← Back");

        addButton.setFont(new Font("Arial", Font.PLAIN, 13));
        removeButton.setFont(new Font("Arial", Font.PLAIN, 13));
        viewButton.setFont(new Font("Arial", Font.PLAIN, 13));
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 13));
        backButton.setFont(new Font("Arial", Font.PLAIN, 13));

        addButton.addActionListener(e -> addRoot());
        removeButton.addActionListener(e -> removeRoot());
        viewButton.addActionListener(e -> viewRootDetails());
        refreshButton.addActionListener(e -> refreshRootList());
        backButton.addActionListener(e -> {
            new TeacherDashboardFrame(manager);
            dispose();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);
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
            refreshRootList();
        }
    }

    private void refreshRootList() {
        listModel.clear();
        if (currentLevel != null) {
            List<String> roots = currentLevel.getAllRoots();
            for (String root : roots) {
                listModel.addElement(root);
            }
        }
    }

    private void addRoot() {
        if (currentLevel == null) {
            JOptionPane.showMessageDialog(this, "Please select a level first!");
            return;
        }

        String root = JOptionPane.showInputDialog(this,
            "Enter the new root (3 Arabic letters):",
            "Add Root",
            JOptionPane.PLAIN_MESSAGE);

        if (root != null && !root.trim().isEmpty()) {
            root = root.trim();
            
            if (root.length() != 3) {
                JOptionPane.showMessageDialog(this,
                    "Root must be exactly 3 letters!",
                    "Invalid Root", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if root already exists
            if (currentLevel.searchRoot(root) != null) {
                JOptionPane.showMessageDialog(this,
                    "This root already exists in the level!",
                    "Duplicate Root", JOptionPane.WARNING_MESSAGE);
                return;
            }

            currentLevel.addRoot(root);
            refreshRootList();
            
            JOptionPane.showMessageDialog(this,
                "Root '" + root + "' added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeRoot() {
        if (currentLevel == null) {
            JOptionPane.showMessageDialog(this, "Please select a level first!");
            return;
        }

        String selected = rootList.getSelectedValue();
        
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a root to remove!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove the root '" + selected + "'?",
            "Confirm Remove",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            currentLevel.removeRoot(selected);
            refreshRootList();
            JOptionPane.showMessageDialog(this, "Root removed successfully!");
        }
    }

    private void viewRootDetails() {
        if (currentLevel == null) {
            JOptionPane.showMessageDialog(this, "Please select a level first!");
            return;
        }

        String selected = rootList.getSelectedValue();
        
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a root!");
            return;
        }

        // Get root data
        org.morpho.RootData rootData = currentLevel.searchRoot(selected);
        
        if (rootData != null) {
            StringBuilder details = new StringBuilder();
            details.append("Root: ").append(rootData.root).append("\n\n");
            details.append("Generated Derivatives:\n");
            
            if (rootData.derivatives.isEmpty()) {
                details.append("  (No derivatives generated yet)\n");
            } else {
                for (String patternName : rootData.derivatives.keySet()) {
                    String word = rootData.derivatives.get(patternName);
                    details.append("  ").append(patternName).append(" → ").append(word).append("\n");
                }
            }

            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane,
                "Root Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}