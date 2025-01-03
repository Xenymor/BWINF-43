package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class StateTracker {
    private final Map<Vector4, VectorMove> previous;
    private final BitSet visited;


    public StateTracker() {
        previous = new HashMap<>();
        visited = new BitSet(Integer.MAX_VALUE);
    }

    public VectorMove get(final Vector4 vector) {
        return previous.get(vector);
    }

    public boolean contains(final Vector4 vector) {
        return visited.get(getIndex(vector));
    }

    public void put(final Vector4 key, final VectorMove value) {
        previous.put(key, value);
        visited.set(getIndex(key));
    }

    private int getIndex(final Vector4 key) {
        return ((((key.x << 8) + key.y << 8) + key.z << 8) + key.w);
    }
}
