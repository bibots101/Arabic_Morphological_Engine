package org.morpho;

import java.util.List;

public class Pattern {
    public String name;
    public List<String> template;

    public Pattern(String name, List<String> template) {
        this.name = name;
        this.template = template;
    }
    public int verifyPattern(Pattern pattern) {
        boolean hasF = false;
        boolean hasAin = false;
        boolean hasLam = false;
        int lCounter = 0;

        for (String s : pattern.template) {
            if (s.equals("ف")) hasF = true;
            if (s.equals("ع")) hasAin = true;
            if (s.equals("ل")) {
                hasLam = true;
                lCounter++;
            }
        }

        if (hasF && hasAin && hasLam) {
            if (lCounter >= 2)
                return 4;
            return 3;
        }
        else{
            return 0;
        }

    }
}