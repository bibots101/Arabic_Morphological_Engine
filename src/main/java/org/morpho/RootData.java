package org.morpho;

import java.util.HashMap;
import java.util.Map;

public class RootData {
    public String root;
    public Map<String, String> derivatives;

    public RootData(String root) {
        this.root = root;
        this.derivatives = new HashMap<>();
    }
}
