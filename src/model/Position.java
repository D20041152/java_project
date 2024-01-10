package model;

public class Position {
    public final double x;
    public final double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Position updateZoom(Position pivot, Double factor) {
        Position coordinatesInPivot = this.minus(pivot);
        Position shift = coordinatesInPivot.times(factor).minus(coordinatesInPivot);
        return this.plus(shift);
    }

    public Position minus(Position other) {
        return new Position(this.x - other.x, this.y - other.y);
    }

    public Position plus(Position other) {
        return new Position(this.x + other.x, this.y + other.y);
    }

    public Position times(Double factor) {
        return new Position(this.x * factor, this.y * factor);
    }

    public double magnitude() {
        return this.x * this.x + this.y * this.y;
    }

    public double magnitude(Position other) {
        return this.minus(other).magnitude();
    }

    public double distance() {
        return Math.sqrt(this.magnitude());
    }

    public double distance(Position other) {
        return Math.sqrt(this.magnitude(other));
    }

    public Position normalize() {
        return this.times(1.0 / this.distance());
    }

    public Position perpendicular() {
        return new Position(this.y, -this.x);
    }

    public int getIntX() {
        return (int)Math.round(this.x);
    }

    public int getIntY() {
        return (int)Math.round(this.y);
    }
}
