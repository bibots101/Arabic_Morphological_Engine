package org.morpho;

import org.morpho.level.LevelManager;
import org.morpho.ui.MainMenuFrame;

/**
 * Main entry point for the Arabic Morphological Learning System.
 * 
 * ARCHITECTURE: Shared Data Approach
 * - All levels use the same data/roots.txt and data/patterns.txt
 * - Teachers control difficulty by enabling/disabling patterns per level
 * - Simple, clean, and uses your existing data files
 */
public class MainWithGUI {

    public static void main(String[] args) {
        // Set system look and feel for better UI
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If fails, use default look and feel
        }

        // Create level manager
        LevelManager manager = new LevelManager();

        // Create 3 levels - all using the same data files
        // Teachers will then configure which patterns are enabled per level
        
        manager.addLevel("Beginner Level",
                "src/main/data/roots.txt",
                "src/main/data/patterns.txt");

        manager.addLevel("Intermediate Level",
                "src/main/data/roots.txt",
                "src/main/data/patterns.txt");

        manager.addLevel("Advanced Level",
                "src/main/data/roots.txt",
                "src/main/data/patterns.txt");

        // Launch GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainMenuFrame(manager);
        });
        
        // Console output
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  Arabic Morphological Learning System        ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("✓ GUI launched successfully!");
        System.out.println("✓ Data source: src/main/data/");
        System.out.println("✓ Levels created: 3 (Beginner, Intermediate, Advanced)");
        System.out.println();
        System.out.println("Next Steps:");
        System.out.println("  1. Go to Teacher Mode");
        System.out.println("  2. Manage Patterns → Disable complex patterns in Beginner");
        System.out.println("  3. Test in Student Mode");
        System.out.println();
        System.out.println("Tip: All levels share the same data files.");
        System.out.println("   Teachers control difficulty via pattern enable/disable.");
        System.out.println("═══════════════════════════════════════════════");
    }
}