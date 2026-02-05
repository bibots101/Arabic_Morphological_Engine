package org.morpho;

import com.sun.tools.jconsole.JConsoleContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static AVLTree tree = new AVLTree();
    static AVLNode rootNode = tree.root;
    static HashTable patterns = new HashTable();

    public static void loadRoots() throws Exception {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("src/main/data/roots.txt"), StandardCharsets.UTF_8));
        String line;
        line = br.readLine();
        while (line != null) {
            rootNode = tree.insert(rootNode, new RootData(line.trim()));
            line = br.readLine();
        }
        br.close();
    }

    public static void loadPatterns() throws Exception {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("src/main/data/patterns.txt"), StandardCharsets.UTF_8));
        String line;
        line = br.readLine();
        while (line != null) {
            String[] parts = line.split(":");
            patterns.insert(
                    parts[0],
                    new Pattern(parts[0], Arrays.asList(parts[1].split(" ")))
            );
            line = br.readLine();
        }
        br.close();
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

            if (c.equals("1")) {
                System.out.print("Nouvelle racine: ");
                String r = sc.nextLine();
                rootNode = tree.insert(rootNode, new RootData(r));
                System.out.println("Racine ajoutée.");
            }

            else if (c.equals("2")) {
                System.out.print("Racine à supprimer: ");
                String r = sc.nextLine();
                rootNode = tree.delete(rootNode, r);
                System.out.println("Suppression terminée.");
            }

            else if (c.equals("3")) {
                System.out.print("Racine à rechercher: ");
                String r = sc.nextLine();

                RootData d = tree.search(rootNode, r);
                if (d != null)
                    System.out.println("Racine trouvée: " + d.root);
                else
                    System.out.println("Racine introuvable.");
            }

            else if (c.equals("4")) {
                tree.display(rootNode);
            }

            else if (c.equals("0")) {
                return;
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

            if (c.equals("1")) {
                System.out.print("Nom du schème: ");
                String name = sc.nextLine();

                System.out.print("Formes (séparées par espace): ");
                List<String> forms = Arrays.asList(sc.nextLine().split(" "));

                patterns.addPattern(name, new Pattern(name, forms));
                System.out.println("Schème ajouté.");
            }

            else if (c.equals("2")) {
                System.out.print("Nom du schème à modifier: ");
                String name = sc.nextLine();

                System.out.print("Nouvelles formes: ");
                List<String> forms = Arrays.asList(sc.nextLine().split(" "));

                boolean ok = patterns.updatePattern(name, new Pattern(name, forms));

                if (ok) System.out.println("Schème modifié.");
                else System.out.println("Schème introuvable.");
            }

            else if (c.equals("3")) {
                System.out.print("Nom du schème à supprimer: ");
                String name = sc.nextLine();

                boolean ok = patterns.deletePattern(name);

                if (ok) System.out.println("Schème supprimé.");
                else System.out.println("Schème introuvable.");
            }

            else if (c.equals("4")) {
                for (Pattern p : patterns.values()) {
                    System.out.println(p.name + " -> " + p.template);
                }
            }

            else if (c.equals("0")) {
                return;
            }
        }
    }


    static void main(String[] args) throws Exception {
        loadRoots();
        loadPatterns();

        Scanner sc = new Scanner(System.in, "UTF-8");

        label:
        while (true) {
            System.out.println("\n1. Gestion des racines");
            System.out.println("2. Gestion des schèmes");
            System.out.println("3. Générer dérivés");
            System.out.println("4. Valider mot");
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
                        System.out.println("Racine introuvable");
                        continue;
                    }

                    for (Pattern p : patterns.values()) {
                        String w = MorphEngine.generateWord(r, p);
                        d.derivatives.put(w, d.derivatives.getOrDefault(w, 0) + 1);
                        System.out.println(p.name + " → " + w);
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
                case "0":
                    break label;
            }
        }
    }
}
