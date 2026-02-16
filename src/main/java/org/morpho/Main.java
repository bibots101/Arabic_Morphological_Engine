package org.morpho;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static AVLTree tree = new AVLTree();
    static AVLNode rootNode = tree.root;
    static HashTable patterns = new HashTable();

    public static void loadRoots() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("src/main/data/roots.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) { // Skip empty lines
                    rootNode = tree.insert(rootNode, new RootData(trimmedLine));
                }
            }
            System.out.println("Roots loaded successfully.");
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Roots file not found");
            System.err.println("Please ensure the data directory exists with the roots.txt file.");
        } catch (IOException e) {
            System.err.println("ERROR: Failed to read roots file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error while loading roots: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadPatterns() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("src/main/data/patterns.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) continue; // Skip empty lines and comments

                String[] parts = trimmedLine.split(":");
                if (parts.length != 2) {
                    System.err.println("WARNING: Invalid pattern format (expected 'name:template'): " + trimmedLine);
                    continue;
                }

                String patternName = parts[0].trim();
                String[] templateParts = parts[1].trim().split(" ");
                if (templateParts.length == 0) {
                    System.err.println("WARNING: Pattern '" + patternName + "' has no template elements.");
                    continue;
                }

                patterns.addPattern(
                        patternName,
                        new Pattern(patternName, Arrays.asList(templateParts))
                );
            }
            System.out.println("✓ Patterns loaded successfully.");
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Patterns file not found");
            System.err.println("Please ensure the data directory exists with the patterns.txt file.");
        } catch (IOException e) {
            System.err.println("ERROR: Failed to read patterns file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error while loading patterns: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void rootsMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Gestion des racines ---");
            System.out.println("1. Ajouter racine");
            System.out.println("2. Supprimer racine");
            System.out.println("3. Rechercher racine");
            System.out.println("4. Afficher racines");
            System.out.println("0. Retour");

            String c = sc.nextLine();

            switch (c) {
                case "1" -> {
                    System.out.print("Nouvelle racine: ");
                    String r = sc.nextLine();
                    rootNode = tree.insert(rootNode, new RootData(r));
                    System.out.println("Racine ajoutée.");
                }
                case "2" -> {
                    System.out.print("Racine à supprimer: ");
                    String r = sc.nextLine();
                    rootNode = tree.delete(rootNode, r);
                    System.out.println("Suppression terminée.");
                }
                case "3" -> {
                    System.out.print("Racine à rechercher: ");
                    String r = sc.nextLine();

                    RootData d = tree.search(rootNode, r);
                    if (d != null)
                        System.out.println("Racine trouvée: " + d.root);
                    else
                        System.out.println("Racine introuvable.");
                }
                case "4" -> tree.display(rootNode);
                case "0" -> {
                    return;
                }
            }
        }
    }


    static void patternMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Gestion des schèmes ---");
            System.out.println("1. Ajouter schème");
            System.out.println("2. Modifier schème");
            System.out.println("3. Supprimer schème");
            System.out.println("4. Afficher schèmes");
            System.out.println("0. Retour");

            String c = sc.nextLine();

            switch (c) {
                case "1" -> {
                    System.out.print("Nom du schème: ");
                    String name = sc.nextLine();

                    System.out.print("Formes (séparées par espace): ");
                    List<String> forms = Arrays.asList(sc.nextLine().split(" "));

                    patterns.addPattern(name, new Pattern(name, forms));
                    System.out.println("Schème ajouté.");
                }
                case "2" -> {
                    System.out.print("Nom du schème à modifier: ");
                    String name = sc.nextLine();

                    System.out.print("Nouvelles formes: ");
                    List<String> forms = Arrays.asList(sc.nextLine().split(" "));

                    boolean ok = patterns.updatePattern(name, new Pattern(name, forms));

                    if (ok) System.out.println("Schème modifié.");
                    else System.out.println("Schème introuvable.");
                }
                case "3" -> {
                    System.out.print("Nom du schème à supprimer: ");
                    String name = sc.nextLine();

                    boolean ok = patterns.deletePattern(name);

                    if (ok) System.out.println("Schème supprimé.");
                    else System.out.println("Schème introuvable.");
                }
                case "4" -> {
                    for (Pattern p : patterns.values()) {
                        System.out.println(p.name + " -> " + p.template);
                    }
                }
                case "0" -> {
                    return;
                }
            }
        }
    }


    public static void main(String[] args) {
        loadRoots();
        loadPatterns();

        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

        label:
        while (true) {
            System.out.println("\n--- Menu Principal ---");
            System.out.println("1. Gestion des racines");
            System.out.println("2. Gestion des schèmes");
            System.out.println("3. Générer dérivés");
            System.out.println("4. Valider mot");
            System.out.println("5. Générer mot spécifique");
            System.out.println("0. Quitter");

            String c = sc.nextLine();

            switch (c) {
                case "1":
                    rootsMenu(sc);
                    break;
                case "2":
                    patternMenu(sc);
                    break;
                case "3": {
                    System.out.print("Racine: ");
                    String r = sc.nextLine();
                    RootData d = tree.search(rootNode, r);

                    if (d == null) {
                        System.out.println("Racine introuvable. Ajouter ? (o/n)");
                        String rep = sc.nextLine();

                        if (rep.equalsIgnoreCase("o")) {
                            rootNode = tree.insert(rootNode, new RootData(r));
                            d = tree.search(rootNode, r);
                            System.out.println("Racine ajoutée.");
                        }
                    }

                    for (Pattern p : patterns.values()) {
                        if (d != null && d.derivatives.containsKey(p.name)) {
                            String w = d.derivatives.get(p.name);
                            if(w == null) continue;
                            System.out.println(r + " + " + p.name + " → "
                                    + w + " (déjà existant)");
                        }
                        else {
                            String w = MorphEngine.generateWord(r, p);
                            if(w == null) continue;
                            if (d != null) d.derivatives.put(p.name, w);

                            System.out.println(r + " + " + p.name + " → "
                                    + w + " (généré)");
                        }

                    }
                    break;
                }
                case "4": {
                    System.out.print("Mot: ");
                    String w = sc.nextLine();
                    System.out.print("Racine: ");
                    String r = sc.nextLine();

                    String p = MorphEngine.validate(w, r, patterns);
                    if (p != null) {
                        System.out.println("OUI (" + p + ")");
                    } else {
                        System.out.println("NON");
                    }

                    break;
                }
                case "5": {
                    System.out.print("Racine: ");
                    String r = sc.nextLine();

                    System.out.print("Nom du schème: ");
                    String pname = sc.nextLine();

                    Pattern selected = null;
                    for (Pattern p : patterns.values()) {
                        if (p.name.equals(pname)) {
                            selected = p;
                            break;
                        }
                    }

                    if (selected == null) {
                        System.out.println("Schème introuvable.");
                        break;
                    }


                    RootData d = tree.search(rootNode, r);

                    if (d == null) {
                        String w = MorphEngine.generateWord(r, selected);
                        System.out.println("Racine: "+ r +"Shéma morphologique: "+selected.name+ "--> Mot généré: " + w);

                        System.out.println("Racine introuvable. Ajouter ? (o/n)");
                        String rep = sc.nextLine();

                        if (rep.equalsIgnoreCase("o")) {
                            rootNode = tree.insert(rootNode, new RootData(r));
                            d = tree.search(rootNode, r);
                            d.derivatives.put(selected.name, w);
                            System.out.println("Racine ajoutée.");
                        }
                    }
                    else{
                        if (d.derivatives.containsKey(selected.name)) {
                            System.out.println("Mot existant: "
                                    + d.derivatives.get(selected.name));
                        } else {
                            String w = MorphEngine.generateWord(r, selected);
                            d.derivatives.put(selected.name, w);
                            System.out.println("Mot généré: " + w);
                        }
                    }
                    break;
                }

                case "0":
                    break label;
            }
        }
    }
}
