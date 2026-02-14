package org.morpho.level;

import org.morpho.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Represents a learning level in the pedagogical system.
 * All levels share the same data/roots.txt and data/patterns.txt
 * but have different pattern enable/disable configurations.
 */
public class Level {
    private String name;
    private AVLTree rootTree;
    private AVLNode rootNode;
    private HashTable patternTable;
    private Set<String> enabledPatterns;
    private Statistics stats;

    /**
     * Create a level that loads from shared data files
     * @param name Level name (e.g., "Beginner", "Intermediate")
     * @param rootsPath Path to roots.txt (usually "src/main/data/roots.txt")
     * @param patternsPath Path to patterns.txt (usually "src/main/data/patterns.txt")
     */
    public Level(String name, String rootsPath, String patternsPath) {
        this.name = name;
        this.rootTree = new AVLTree();
        this.rootNode = null;
        this.patternTable = new HashTable();
        this.enabledPatterns = new HashSet<>();
        this.stats = new Statistics();
        
        loadRoots(rootsPath);
        loadPatterns(patternsPath);
        
        // Enable all patterns by default
        // Teachers can disable specific patterns for lower levels
        for (Pattern p : patternTable.values()) {
            enabledPatterns.add(p.name);
        }
    }

    private void loadRoots(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("⚠ Roots file not found: " + path);
                System.err.println("  Create the file or add roots via Teacher Mode.");
                return;
            }
            
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    rootNode = rootTree.insert(rootNode, new RootData(trimmed));
                }
            }
            br.close();
            System.out.println("✓ Loaded roots for level: " + name);
        } catch (Exception e) {
            System.err.println("✗ Error loading roots: " + e.getMessage());
        }
    }

    private void loadPatterns(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("⚠ Patterns file not found: " + path);
                System.err.println("  Create the file or add patterns via Teacher Mode.");
                return;
            }
            
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && trimmed.contains(":")) {
                    String[] parts = trimmed.split(":", 2);
                    if (parts.length >= 2) {
                        String patternName = parts[0].trim();
                        String templateStr = parts[1].trim();
                        List<String> template = Arrays.asList(templateStr.split("\\s+"));
                        patternTable.addPattern(patternName, new Pattern(patternName, template));
                    }
                }
            }
            br.close();
            System.out.println("✓ Loaded patterns for level: " + name);
        } catch (Exception e) {
            System.err.println("✗ Error loading patterns: " + e.getMessage());
        }
    }

    // ==================== ROOT MANAGEMENT ====================
    
    public void addRoot(String root) {
        rootNode = rootTree.insert(rootNode, new RootData(root));
    }

    public void removeRoot(String root) {
        rootNode = rootTree.delete(rootNode, root);
    }

    public RootData searchRoot(String root) {
        return rootTree.search(rootNode, root);
    }

    public List<String> getAllRoots() {
        List<String> roots = new ArrayList<>();
        collectRoots(rootNode, roots);
        return roots;
    }

    private void collectRoots(AVLNode node, List<String> roots) {
        if (node != null) {
            collectRoots(node.left, roots);
            roots.add(node.data.root);
            collectRoots(node.right, roots);
        }
    }

    // ==================== PATTERN MANAGEMENT ====================
    
    public void addPattern(String name, List<String> template) {
        patternTable.addPattern(name, new Pattern(name, template));
        enabledPatterns.add(name);
    }

    public void removePattern(String name) {
        patternTable.deletePattern(name);
        enabledPatterns.remove(name);
    }

    public List<Pattern> getAllPatterns() {
        return patternTable.values();
    }

    public List<Pattern> getEnabledPatterns() {
        List<Pattern> enabled = new ArrayList<>();
        for (Pattern p : patternTable.values()) {
            if (enabledPatterns.contains(p.name)) {
                enabled.add(p);
            }
        }
        return enabled;
    }

    public void enablePattern(String patternName) {
        enabledPatterns.add(patternName);
    }

    public void disablePattern(String patternName) {
        enabledPatterns.remove(patternName);
    }

    public boolean isPatternEnabled(String patternName) {
        return enabledPatterns.contains(patternName);
    }

    // ==================== WORD GENERATION/VALIDATION ====================
    
    /**
     * Generate a word using an enabled pattern only
     */
    public String generateWord(String root, String patternName) {
        if (!enabledPatterns.contains(patternName)) {
            return null; // Pattern not enabled for this level
        }
        
        Pattern pattern = null;
        for (Pattern p : patternTable.values()) {
            if (p.name.equals(patternName)) {
                pattern = p;
                break;
            }
        }
        
        if (pattern == null) return null;
        return MorphEngine.generateWord(root, pattern);
    }

    /**
     * Validate if a word belongs to a root
     */
    public String validateWord(String word, String root) {
        return MorphEngine.validate(word, root, patternTable);
    }

    // ==================== GETTERS ====================
    
    public String getName() {
        return name;
    }

    public Statistics getStats() {
        return stats;
    }

    public AVLNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(AVLNode node) {
        this.rootNode = node;
    }
}