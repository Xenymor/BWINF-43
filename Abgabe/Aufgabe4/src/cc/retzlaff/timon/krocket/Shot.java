package cc.retzlaff.timon.round1.krocket;

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

    public double square(double v) {
        return v * v;
    }

    @Override
    public String toString() {
        final double diffX = getDiffX();
        final double diffY = getDiffY();
        double angle = diffX == 0 ? diffY > 0 ? 90 : -90 : Math.atan(diffY / diffX) * (360 / (2 * Math.PI));
        return "x1=" + x1 + ";x2=" + x2 + ";angle:" + angle + "°";
    }
}
