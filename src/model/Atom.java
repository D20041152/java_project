package model;

import model.AtomType;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Atom {
    public Position position;
    public final AtomType type;

    public Atom(Position position, AtomType type) {
        this.position = position;
        this.type = type;
    }

    public abstract void paintTo(Graphics2D var1, boolean var2, double var3);

    public abstract boolean intersect(Position var1, double var2);

    public void serialize(FileWriter writer) throws IOException {
        writer.write(this.type.name() + "\n");
        writer.write(this.position.x + " " + this.position.y + "\n");
    }
}
