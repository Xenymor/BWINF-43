package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import java.util.Objects;

public class Vector2 {
    int x;
    int y;

    public Vector2(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public static int manhattanDist(final int x1, final int y1, final int x2, final int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Vector2 vector2 = (Vector2) o;
        return x == vector2.x && y == vector2.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ',' + y + ')';
    }

    @Override
    protected Vector2 clone() {
        return new Vector2(x, y);
    }
}
