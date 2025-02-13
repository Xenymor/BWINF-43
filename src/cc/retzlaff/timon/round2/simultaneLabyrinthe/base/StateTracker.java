package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;

import java.util.HashMap;
import java.util.Map;

public class StateTracker {
    private final Map<State, PositionData> previous;
    private final MyBitSet visited;

    public StateTracker() {
        previous = new HashMap<>();
        visited = new MyBitSet(1L << 35);
    }

    public PositionData get(final State vector) {
        return previous.get(vector);
    }

    public boolean hasSeen(final State vector) {
        return visited.get(getIndex(vector));
    }

    public void put(final State key, final PositionData value) {
        previous.put(key, value);
        visited.set(getIndex(key));
    }

    public void removeFromMap(final State key) {
        previous.remove(key);
    }

    private long getIndex(final State key) {
        return ((((((long) key.x << 8) + key.y << 8) + key.z << 8) + key.w) << 3) + key.jumpCount;
    }

    public int getMapSize() {
        return previous.size();
    }

    private static class MyBitSet {
        private final long[] words;

        public MyBitSet(final long capacity) {
            this.words = new long[(int) (capacity / 64 + 1)];
        }

        public void set(final long bitIndex) {
            words[(int) (bitIndex >>> 6)] |= (1L << bitIndex);
        }

        public boolean get(final long index) {
            return (words[(int) (index >>> 6)] & (1L << index)) != 0;
        }
    }
}
