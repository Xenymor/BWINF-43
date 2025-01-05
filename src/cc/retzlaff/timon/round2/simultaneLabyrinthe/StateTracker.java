package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import java.util.HashMap;
import java.util.Map;

public class StateTracker {
    private final Map<Vector4, PositionData> previous;
    private final MyBitSet visited;

    public StateTracker() {
        previous = new HashMap<>();
        visited = new MyBitSet(0x1_0000_0000L);
    }

    public PositionData get(final Vector4 vector) {
        return previous.get(vector);
    }

    public boolean hasSeen(final Vector4 vector) {
        return visited.get(getIndex(vector));
    }

    public void put(final Vector4 key, final PositionData value) {
        previous.put(key, value);
        visited.set(getIndex(key));
    }

    public void removeFromMap(final Vector4 key) {
        previous.remove(key);
    }

    private long getIndex(final Vector4 key) {
        return (((((long) key.x << 8) + key.y << 8) + key.z << 8) + key.w);
    }

    private static class MyBitSet {
        private final long[] words;

        public MyBitSet(final long capacity) {
            this.words = new long[(int) (capacity / 64 + 1)];
        }

        public void set(final long bitIndex) {
            words[(int) (bitIndex >> 6)] |= (1L << bitIndex);
        }

        public boolean get(final long index) {
            return (words[(int) (index >> 6)] & (1L << index)) != 0;
        }
    }
}
