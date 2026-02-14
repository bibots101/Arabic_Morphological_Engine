package org.morpho.level;

import java.io.File;
import java.util.*;

/**
 * Manages multiple learning levels in the system.
 * Handles level creation, deletion, and access.
 */
public class LevelManager {
    private Map<String, Level> levels;

    public LevelManager() {
        this.levels = new LinkedHashMap<>();
    }

    /**
     * Add a new level with specified data files
     */
    public void addLevel(String name, String rootsPath, String patternsPath) {
        Level level = new Level(name, rootsPath, patternsPath);
        levels.put(name, level);
    }

    /**
     * Remove a level by name
     */
    public void removeLevel(String name) {
        levels.remove(name);
    }

    /**
     * Get a specific level
     */
    public Level getLevel(String name) {
        return levels.get(name);
    }

    /**
     * Get all level names
     */
    public List<String> listLevels() {
        return new ArrayList<>(levels.keySet());
    }

    /**
     * Check if a level exists
     */
    public boolean hasLevel(String name) {
        return levels.containsKey(name);
    }

    /**
     * Create data directories for a new level
     */
    public boolean createLevelDirectories(String levelName) {
        String basePath = "src/main/data/" + levelName;
        File levelDir = new File(basePath);
        
        if (!levelDir.exists()) {
            boolean created = levelDir.mkdirs();
            if (created) {
                // Create empty files
                try {
                    new File(basePath + "/roots.txt").createNewFile();
                    new File(basePath + "/patterns.txt").createNewFile();
                    return true;
                } catch (Exception e) {
                    System.err.println("Error creating files: " + e.getMessage());
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Get the count of levels
     */
    public int getLevelCount() {
        return levels.size();
    }
}