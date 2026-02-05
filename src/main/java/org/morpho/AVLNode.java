package org.morpho;

public class AVLNode {
    RootData data;
    AVLNode left;
    AVLNode right;

    public AVLNode(RootData data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}
