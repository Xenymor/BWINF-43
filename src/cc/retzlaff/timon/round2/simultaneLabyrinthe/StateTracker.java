package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import java.util.BitSet;

public class StateTracker {
    private final VectorMove[] previous;
    private final BitSet visited;


    public StateTracker() {
        previous = new VectorMove[Integer.MAX_VALUE/2];
        visited = new BitSet(Integer.MAX_VALUE);
    }

    public VectorMove get(final Vector4 vector) {
        return previous[getIndex(vector)];
    }

    public boolean contains(final Vector4 vector) {
        return visited.get(getIndex(vector));
    }

    public void put(final Vector4 key, final VectorMove value) {
        try {
            previous[getIndex(key)] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println(key);
            throw e;
        }
        visited.set(getIndex(key));
    }

    private int getIndex(final Vector4 key) {
        return ((((key.x << 7) + key.y << 7) + key.z << 7) + key.w);
    }
}
