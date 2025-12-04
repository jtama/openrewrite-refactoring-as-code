package io.github.jtama.openrewrite.model;

/**
 * Represents a node in the D3.js graph, corresponding to a Java class.
 */
public class Node {
    private final String id;
    private final String group;
    private int size;

    /**
     * Constructs a new Node.
     * @param id The fully qualified name of the class.
     * @param group The package name of the class.
     */
    public Node(String id, String group) {
        this.id = id;
        this.group = group;
        this.size = 1; // Start with a base size
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public int getSize() {
        return size;
    }

    /**
     * Increments the size of the node, typically representing an additional connection.
     */
    public void incrementSize() {
        this.size++;
    }
}