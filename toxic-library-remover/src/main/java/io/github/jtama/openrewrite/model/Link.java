package io.github.jtama.openrewrite.model;

/**
 * Represents a link between two nodes in the D3.js graph, corresponding to an interaction between two Java classes.
 */
public class Link {
    private final String source;
    private final String target;
    private int weight;

    /**
     * Constructs a new Link.
     * @param source The fully qualified name of the source class.
     * @param target The fully qualified name of the target class.
     */
    public Link(String source, String target) {
        this.source = source;
        this.target = target;
        this.weight = 1; // Start with a base weight
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * Increments the weight of the link, representing an additional interaction between the two classes.
     */
    public void incrementWeight() {
        this.weight++;
    }
}