package model;

import model.Atom;
import model.Position;

import java.awt.*;

public class RoundAtom extends Atom {
    private final Color color;
    private final int radius;

    public RoundAtom(Position position, Color color, int radius, model.AtomType type) {
        super(position, type);
        this.color = color;
        this.radius = radius;
    }

    @Override
    public void paintTo(Graphics2D canvas, boolean selected, double factor) {
        Color startColor = canvas.getColor();
        Stroke startStroke = canvas.getStroke();

        canvas.setStroke(new BasicStroke((float)(3 * factor)));
        canvas.setColor(color);
        canvas.fillOval(toInt(position.x - radius * factor), toInt(position.y - radius * factor), toInt(radius*2 * factor), toInt(radius*2 * factor));

        canvas.setColor(selected ? Color.RED : Color.DARK_GRAY);
        if (selected) canvas.setStroke(new BasicStroke((float)(6 * factor)));
        canvas.drawOval(toInt(position.x - radius * factor), toInt(position.y - radius * factor), toInt(radius*2 * factor), toInt(radius*2 * factor));

        canvas.setColor(startColor);
        canvas.setStroke(startStroke);
    }

    @Override
    public boolean intersect(Position position, double factor) {
        return this.position.magnitude(position) < radius * radius * factor * factor;
    }

    private static int toInt(double value) {
        return (int) Math.round(value);
    }
}
