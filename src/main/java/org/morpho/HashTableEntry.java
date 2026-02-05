package org.morpho;

public class HashTableEntry {
    public String key;
    public Pattern value;

    public HashTableEntry(String k, Pattern v) {
        key = k;
        value = v;
    }
}
