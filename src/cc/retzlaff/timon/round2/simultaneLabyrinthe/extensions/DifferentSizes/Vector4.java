package cc.retzlaff.timon.round2.simultaneLabyrinthe.extensions.DifferentSizes;

import java.util.Objects;

public class Vector4 {
    public final int x;
    public final int y;
    public final int z;
    public final int w;

    public Vector4(final int x, final int y, final int z, final int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4(final Vector2 v1, final Vector2 v2) {
        x = v1.x;
        y = v1.y;
        z = v2.x;
        w = v2.y;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Vector4 vector4 = (Vector4) o;
        return x == vector4.x && y == vector4.y && z == vector4.z && w == vector4.w;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return "(" + x + ',' + y + ',' + z + ',' + w + ')';
    }
}
