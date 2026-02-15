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
        return result.toString();
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
                if (r.equals(root))
                    return p.name;
            }
        }
        return null;
    }
}
