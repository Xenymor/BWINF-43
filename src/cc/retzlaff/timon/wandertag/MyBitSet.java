package cc.retzlaff.timon.wandertag;

public class MyBitSet {
    private final long[] words;

    public MyBitSet(final int longCount) {
        this.words = new long[longCount];
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public void copyOr(MyBitSet first, MyBitSet second) {
        final long[] firstWords = first.words;
        final long[] secondWords = second.words;
        for (int i = 0; i < words.length; i++) {
            this.words[i] = firstWords[i] | secondWords[i];
        }
    }

    public void set(final int bitIndex) {
        words[bitIndex >> 6] |= (1L << bitIndex);
    }

    public int orCardinality(final MyBitSet other) {
        int count = 0;
        @SuppressWarnings("UnnecessaryLocalVariable") final long[] otherWords = other.words;
        final int length = words.length;
        for (int i = 0; i < length; i++) {
            count += Long.bitCount(words[i] | otherWords[i]);
        }
        return count;
    }
}