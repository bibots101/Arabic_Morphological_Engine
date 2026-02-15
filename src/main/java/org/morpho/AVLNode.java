package org.morpho;

public class AVLNode {
    public RootData data;
    public AVLNode left;
    public AVLNode right;

    public AVLNode(RootData data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}