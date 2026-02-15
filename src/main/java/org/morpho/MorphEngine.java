package org.morpho;

import java.util.HashMap;
import java.util.Map;

public class MorphEngine {

    public static String generateWord(String root, Pattern pattern) {
        int patternSize = pattern.verifyPattern(pattern);
        if (root.length() != patternSize)
            return null;

        Map<String, Character> map = new HashMap<>();
        map.put("ف", root.charAt(0));
        map.put("ع", root.charAt(1));
        map.put("ل", root.charAt(2));

        if (root.length() == 4)
            map.put("ل2", root.charAt(3));

        StringBuilder result = new StringBuilder();
        int lCounter = 0;
        for (String s : pattern.template) {
            if (s.equals("ل") && root.length() == 4) {
                lCounter++;
                if (lCounter == 1)
                    result.append(map.get("ل"));
                else
                    result.append(map.get("ل2"));
            } else {
                result.append(map.getOrDefault(s, s.charAt(0)));
            }
        }
        return result.toString();
    }

    public static String validate(String word, String root, HashTable table) {
        for (Pattern p : table.values()) {
            int patternSize = p.verifyPattern(p);
            if (p.template.size() != word.length() || patternSize != root.length())
                continue;

            Map<String, Character> extracted = new HashMap<>();
            boolean ok = true;

            int lCounter = 0;

            for (int i = 0; i < p.template.size(); i++) {
                String t = p.template.get(i);
                char w = word.charAt(i);

                if (t.equals("ف") || t.equals("ع")) {
                    extracted.put(t, w);
                } else if (t.equals("ل")) {
                    lCounter++;
                    if (lCounter == 1)
                        extracted.put("ل", w);
                    else
                        extracted.put("ل2", w);

                } else if (t.charAt(0) != w) {
                    ok = false;
                    break;
                }
            }

            if (!ok)
                continue;

            if (root.length() == 3) {
                String r = ""
                        + extracted.get("ف")
                        + extracted.get("ع")
                        + extracted.get("ل");

                if (r.equals(root))
                    return p.name;
            }

            if (root.length() == 4) {
                if (!extracted.containsKey("ل2"))
                    continue;

                String r =
                        "" + extracted.get("ف")
                                + extracted.get("ع")
                                + extracted.get("ل")
                                + extracted.get("ل2");

                if (r.equals(root))
                    return p.name;
            }
        }

        return null;
    }
}