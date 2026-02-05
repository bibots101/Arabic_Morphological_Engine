package org.morpho;

import java.util.List;

public class Pattern {
    public String name;
    public List<String> template;

    public Pattern(String name, List<String> template) {
        this.name = name;
        this.template = template;
    }
}