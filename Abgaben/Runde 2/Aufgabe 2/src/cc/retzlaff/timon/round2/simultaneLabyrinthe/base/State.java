package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;

import java.util.Objects;

public class State {
    public final int x;
    public final int y;
    public final int z;
    public final int w;
    public final int jumpCount;

    public State(final int x, final int y, final int z, final int w, final int jumpCount) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.jumpCount = jumpCount;
    }

    public State(final Vector2 v1, final Vector2 v2, final int jumpCount) {
        x = v1.x;
        y = v1.y;
        z = v2.x;
        w = v2.y;
        this.jumpCount = jumpCount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final State state = (State) o;
        return x == state.x && y == state.y && z == state.z && w == state.w && jumpCount == state.jumpCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w, jumpCount);
    }

    @Override
    public String toString() {
        return "(" + x + "|" + y + "|" + z + "|" + w + "|" + jumpCount + ")";
    }

    public boolean equalsIgnoreJumpCount(final State other) {
        return x == other.x && y == other.y && z == other.z && w == other.w;
    }
}
