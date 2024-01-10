package model;

import model.AtomType;
import model.Edge;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Molecule {
    public final List<Atom> atoms;
    public final List<Edge> edges;

    public Molecule(List<Atom> atoms, List<Edge> edges) {
        this.atoms = atoms;
        this.edges = edges;
    }

    public Molecule() {
        this.atoms = new ArrayList();
        this.edges = new ArrayList();
    }

    public void addAtom(Atom atom) {
        this.atoms.add(atom);
    }

    public void removeAtom(Atom atom) {
        this.atoms.remove(atom);
        this.edges.removeIf((it) -> {
            return it.end == atom || it.start == atom;
        });
    }

    public void addEdge(Edge newEdge) {
        Iterator var2 = this.edges.iterator();

        Edge edge;
        do {
            if (!var2.hasNext()) {
                this.edges.add(newEdge);
                return;
            }

            edge = (Edge)var2.next();
        } while(!edge.isSameAs(newEdge));

        ++edge.weight;
    }

    public void addEdge(Atom start, Atom end) {
        this.addEdge(new Edge(start, end));
    }

    public void paintTo(Graphics2D canvas, Atom selected, double factor) {
        this.paintAdjacency(canvas, factor);
        this.paintAtoms(canvas, selected, factor);
    }

    private void paintAdjacency(Graphics2D canvas, double factor) {
        Stroke initialStroke = canvas.getStroke();
        Iterator var5 = this.edges.iterator();

        while(var5.hasNext()) {
            Edge edge = (Edge)var5.next();
            this.paintEdge(canvas, edge, factor);
        }

        canvas.setStroke(initialStroke);
    }

    private void paintEdge(Graphics2D canvas, Edge edge, double factor) {
        canvas.setStroke(new BasicStroke((float)((double)(16 / edge.weight) * factor)));
        Position startPosition = edge.start.position;
        Position endPosition = edge.end.position;
        Position shift = endPosition.minus(startPosition).normalize().times(20.0 / (double)edge.weight * factor).perpendicular();
        startPosition = startPosition.plus(shift.times((double)(edge.weight - 1) / -2.0));
        endPosition = endPosition.plus(shift.times((double)(edge.weight - 1) / -2.0));

        for(int i = 0; i < edge.weight; ++i) {
            Position currentStart = startPosition.plus(shift.times((double)i));
            Position currentEnd = endPosition.plus(shift.times((double)i));
            canvas.drawLine(currentStart.getIntX(), currentStart.getIntY(), currentEnd.getIntX(), currentEnd.getIntY());
        }

    }

    private void paintAtoms(Graphics2D canvas, Atom selected, double factor) {
        for(int i = this.atoms.size() - 1; i >= 0; --i) {
            Atom atom = (Atom)this.atoms.get(i);
            atom.paintTo(canvas, atom == selected, factor);
        }

    }

    public void saveTo(FileWriter writer) throws IOException {
        writer.write(this.atoms.size() + "\n");
        Iterator var2 = this.atoms.iterator();

        while(var2.hasNext()) {
            Atom atom = (Atom)var2.next();
            atom.serialize(writer);
        }

        writer.write(this.edges.size() + "\n");
        var2 = this.edges.iterator();

        while(var2.hasNext()) {
            Edge edge = (Edge)var2.next();
            int var10001 = this.indexOfAtom(edge.start);
            writer.write("" + var10001 + " " + this.indexOfAtom(edge.end) + " " + edge.weight + "\n");
        }

    }

    private int indexOfAtom(Atom atom) {
        for(int i = 0; i < this.atoms.size(); ++i) {
            if (this.atoms.get(i) == atom) {
                return i;
            }
        }

        return -1;
    }

    public static Molecule readFrom(BufferedReader reader) throws IOException {
        Molecule result = new Molecule();
        int n = Integer.parseInt(reader.readLine());

        int m;
        String[] positionLine;
        for(m = 0; m < n; ++m) {
            AtomType type = AtomType.valueOf(reader.readLine());
            positionLine = reader.readLine().split(" ");
            double positionX = Double.parseDouble(positionLine[0]);
            double positionY = Double.parseDouble(positionLine[1]);
            Position position = new Position(positionX, positionY);
            result.addAtom(AtomsFactory.create(type, position));
        }

        m = Integer.parseInt(reader.readLine());

        for(int i = 0; i < m; ++i) {
            positionLine = reader.readLine().split(" ");
            int start = Integer.parseInt(positionLine[0]);
            int end = Integer.parseInt(positionLine[1]);
            int weight = Integer.parseInt(positionLine[2]);
            result.addEdge(new Edge((Atom)result.atoms.get(start), (Atom)result.atoms.get(end), weight));
        }

        return result;
    }
}