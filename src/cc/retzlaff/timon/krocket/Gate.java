package cc.retzlaff.timon.krocket;

public class Gate extends Shot {
    public Gate(final double x1, final double y1, final double x2, final double y2) {
        super(x1, y1, x2, y2);
    }

    public double cornerDist(final double x, final double y) {
        return Math.min(getDist(x - x1, y - y1), getDist(x - x2, y - y2));
    }

    @Override
    public String toString() {
        return "Gate{" +
                "x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                '}';
    }
}
