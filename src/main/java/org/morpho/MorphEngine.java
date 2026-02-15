package org.morpho;

import java.util.HashMap;
import java.util.Map;

public class MorphEngine {

    public static String generateWord(String root, Pattern pattern) {
        Map<String, Character> map = new HashMap<>();
        map.put("ف", root.charAt(0));
        map.put("ع", root.charAt(1));
        map.put("ل", root.charAt(2));

        StringBuilder result = new StringBuilder();
        for (String s : pattern.template) {
            result.append(map.getOrDefault(s, s.charAt(0)));
        }
        String word = result.toString();

        word = normalizeWeakVerb(word, root, pattern);
        word = normalizeHamza(word);

        return word;
    }

    private static String normalizeWeakVerb(String word, String root, Pattern p) {

        char second = root.charAt(1);
        char third  = root.charAt(2);

        // -------- AJWAF (middle character weak) --------
        if (second == 'و' || second == 'ي') {

            word = word.replace("وو", "و");
            word = word.replace("يي", "ي");

            if (p.name.equals("فاعل")) {
                word = word.replace("او", "ائ");
            }
        }

        // -------- NAQIS (last character weak) --------
        if (third == 'ي' || third == 'و' || third == 'ى') {

            if (word.endsWith("يي") || word.endsWith("وو")) {
                word = word.substring(0, word.length() - 1);
            }

            if (word.endsWith("ي")) {
                word = word.substring(0, word.length() - 1) + "ى";
            }
        }
        return word;
    }
    private static String normalizeHamza(String word) {

        word = word.replace("اء", "ائ");
        word = word.replace("ايء", "ائ");
        word = word.replace("اوء", "ؤ");

        word = word.replace("يء", "ئ");

        return word;
    }


    public static String validate(String word, String root, HashTable table) {
        for (Pattern p : table.values()) {
            if (p.template.size() != word.length())
                continue;

            Map<String, Character> extracted = new HashMap<>();
            boolean ok = true;

            for (int i = 0; i < p.template.size(); i++) {
                String t = p.template.get(i);
                char w = word.charAt(i);

                if (t.equals("ف") || t.equals("ع") || t.equals("ل"))
                    extracted.put(t, w);
                else if (t.charAt(0) != w) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                String r = "" + extracted.get("ف") + extracted.get("ع") + extracted.get("ل");
                if (rootsEquivalent(r, root))
                    return p.name;
            }
        }
        return null;
    }

    private static boolean rootsEquivalent(String r1, String r2) {

        if (r1.length() != 3 || r2.length() != 3)
            return false;

        for (int i = 0; i < 3; i++) {

            char c1 = r1.charAt(i);
            char c2 = r2.charAt(i);

            if (c1 == c2)
                continue;

            if (isWeak(c1) && isWeak(c2))
                continue;
            if (isHamza(c1) && isHamza(c2))
                continue;

            return false;
        }

        return true;
    }

    private static boolean isWeak(char c) {
        return c == 'و' || c == 'ي' || c == 'ا' || c == 'ى';
    }
    private static boolean isHamza(char c) {
        return c == 'ء' || c == 'أ' || c == 'إ'
                || c == 'ؤ' || c == 'ئ';
    }

}
