package org.morpho;

public class AVLTree {
    public AVLNode root;

    public AVLTree() {
        this.root = null;
    }

    public AVLNode insert(AVLNode node, RootData data) {
        if (node == null) {
            return new AVLNode(data);
        }

        if (data.root.compareTo(node.data.root) < 0) {
            node.left = insert(node.left, data);
        } else {
            node.right = insert(node.right, data);
        }

        return balance(node);
    }

    private int height(AVLNode node) {
        if (node == null)
            return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    private int getBalance(AVLNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }


    private AVLNode balance(AVLNode node) {
        int balance = getBalance(node);

        // Left heavy
        if (balance > 1) {
            if (getBalance(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        // Right heavy
        if (balance < -1) {
            if (getBalance(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        return node;
    }


    private AVLNode rotateLeft(AVLNode z) {
        AVLNode y = z.right;
        AVLNode t2 = y.left;

        y.left = z;
        z.right = t2;

        return y;
    }

    private AVLNode rotateRight(AVLNode z) {
        AVLNode y = z.left;
        AVLNode t3 = y.right;

        y.right = z;
        z.left = t3;

        return y;
    }

    public RootData search(AVLNode node, String rootValue) {
        if (node == null)
            return null;

        if (rootValue.equals(node.data.root))
            return node.data;

        if (rootValue.compareTo(node.data.root) < 0)
            return search(node.left, rootValue);

        return search(node.right, rootValue);
    }

    public AVLNode delete(AVLNode node, String rootValue) {
        if (node == null)
            return null;

        if (rootValue.compareTo(node.data.root) < 0) {
            node.left = delete(node.left, rootValue);
        }
        else if (rootValue.compareTo(node.data.root) > 0) {
            node.right = delete(node.right, rootValue);
        }
        else {
            if (node.left == null)
                return node.right;
            else if (node.right == null)
                return node.left;

            AVLNode successor = minValueNode(node.right);
            node.data = successor.data;
            node.right = delete(node.right, successor.data.root);
        }

        return balance(node);
    }

    private AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    public void display(AVLNode node) {
        if (node != null) {
            display(node.left);
            System.out.println(node.data.root);
            display(node.right);
        }
    }
}
