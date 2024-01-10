package model;

public class Edge {
    public final Atom start;
    public final Atom end;
    public int weight;

    public Edge(Atom start, Atom end, int weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public Edge(Atom start, Atom end) {
        this(start, end, 1);
    }

    public boolean isSameAs(Edge other) {
        return this.start == other.start && this.end == other.end || this.start == other.end && this.end == other.start;
    }
}