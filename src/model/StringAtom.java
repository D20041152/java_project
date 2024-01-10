package model;

import model.Atom;
import model.Position;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class StringAtom extends Atom {

    private final String label;
    private Rectangle2D lastBounds;

    public StringAtom(Position position, String label, model.AtomType type) {
        super(position, type);
        this.label = label;
    }

    @Override
    public void paintTo(Graphics2D canvas, boolean selected, double factor) {
        Color startColor = canvas.getColor();
        Font startFont = canvas.getFont();


        Font font = new Font("TimesRoman", Font.BOLD, (int)Math.round(48 * factor));
        canvas.setFont(font);
        lastBounds = new TextLayout(label, font, canvas.getFontRenderContext()).getBounds();

        int width = (int)Math.round(lastBounds.getWidth() + 25 * factor);
        int height = (int)Math.round(lastBounds.getHeight() + 25 * factor);

        int startX = position.getIntX() - width / 2;
        int startY = position.getIntY() - height / 2;

        canvas.setColor(Color.LIGHT_GRAY);
        canvas.fillRect(startX, startY, width, height);

        canvas.setStroke(new BasicStroke((float)(3 * factor)));
        canvas.setColor(selected ? Color.RED : Color.DARK_GRAY);
        if (selected) canvas.setStroke(new BasicStroke((float)(6 * factor)));

        canvas.drawRect(startX, startY, width, height);

        canvas.setColor(Color.BLACK);
        canvas.drawString(label, startX + width / 5, startY + height * 13 / 16);

        canvas.setColor(startColor);
        canvas.setFont(startFont);
    }

    @Override
    public boolean intersect(Position position, double factor) {
        if (lastBounds == null) return false;

        int width = (int)Math.round(lastBounds.getWidth() + 20 * factor);
        int height = (int)Math.round(lastBounds.getHeight() + 20 * factor);

        int startX = this.position.getIntX() - width / 2;
        int startY = this.position.getIntY() - height / 2;

        int endX = startX + width;
        int endY = startY + height;

        return position.x >= startX && position.x <= endX && position.y >= startY && position.y <= endY;
    }
}
