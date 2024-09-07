package cc.retzlaff.timon.krocket;

public class Shot {
    final double x1;
    final double x2;
    final double y1;
    final double y2;

    public Shot(final double x1, final double y1, final double x2, final double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public double getLength() {
        return getDist(getDiffX(), getDiffY());
    }

    double getDist(final double diffX, final double diffY) {
        return Math.sqrt(square(diffX) + square(diffY));
    }

    public double getDiffX() {
        return x2 - x1;
    }

    public double getDiffY() {
        return y2 - y1;
    }

    public double dist(final double x, final double y) {
        double v1 = -(x2 - x1) * x1 + (x2 - x1) * x;
        double v2 = -(y2 - y1) * y1 + (y2 - y1) * y;
        double v3 = square(x2 - x1) + square(y2 - y1);
        double s = (v1 + v2) / v3;

        double closestX = x1 + getDiffX() * s;
        double closestY = y1 + getDiffY() * s;

        if (closestX <= Math.max(x1, x2) && closestX >= Math.min(x1, x2)
                && closestY <= Math.max(y1, y2) && closestY >= Math.min(y1, y2)) {
            return pointDist(closestX, closestY, x, y);
        } else {
            return Math.min(pointDist(x, y, x1, y1), pointDist(x, y, x2, y2));
        }
    }

    private double pointDist(final double x1, final double y1, final double x2, final double y2) {
        return getDist(x2 - x1, y2 - y1);
    }

    public double square(double v) {
        return v * v;
    }

    @Override
    public String toString() {
        return "Shot{" +
                "x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                '}';
    }
}
