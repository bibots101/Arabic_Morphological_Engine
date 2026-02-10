package org.morpho;

import java.util.ArrayList;
import java.util.List;

public class HashTable {


    private int size = 23;
    private List<HashTableEntry>[] table;

    public HashTable() {
        table = new ArrayList[size];
        for (int i = 0; i < size; i++)
            table[i] = new ArrayList<>();
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % size;
    }

    public void addPattern(String key, Pattern value) {
        int index = hash(key);
        table[index].add(new HashTableEntry(key, value));
    }

    public boolean updatePattern(String key, Pattern newValue) {
        int index = hash(key);
        for (HashTableEntry e : table[index]) {
            if (e.key.equals(key)) {
                e.value = newValue;
                return true;
            }
        }
        return false;
    }

    public boolean deletePattern(String key) {
        int index = hash(key);
        return table[index].removeIf(e -> e.key.equals(key));
    }


    public void insert(String key, Pattern value) {
        int index = hash(key);
        for (HashTableEntry e : table[index]) {
            if (e.key.equals(key)) {
                e.value = value;
                return;
            }
        }
        table[index].add(new HashTableEntry(key, value));
    }

    public List<Pattern> values() {
        List<Pattern> all = new ArrayList<>();
        for (List<HashTableEntry> bucket : table)
            for (HashTableEntry e : bucket)
                all.add(e.value);
        return all;
    }
}
