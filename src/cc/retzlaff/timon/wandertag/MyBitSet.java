package cc.retzlaff.timon.wandertag;

public class MyBitSet {
    private static final int ADDRESS_BITS_PER_WORD = 6;
    private final long[] words;

    public MyBitSet(final int longCount) {
        this.words = new long[longCount];
    }

    public void copy(MyBitSet other) {
        System.arraycopy(other.words, 0, words, 0, words.length);
    }

    public void or(MyBitSet other) {
        for (int i = 0; i < words.length; i++)
            words[i] |= other.words[i];
    }

    public void copyOr(MyBitSet first, MyBitSet second) {
        final long[] firstWords = first.words;
        final long[] secondWords = second.words;
        for (int i = 0; i < words.length; i++) {
            this.words[i] = firstWords[i] | secondWords[i];
        }
    }

    public void set(final int bitIndex) {
        words[bitIndex >> ADDRESS_BITS_PER_WORD] |= (1L << bitIndex);
    }

    public int orCardinality(final MyBitSet other) {
        int count = 0;
        final long[] otherWords = other.words;
        final int length = words.length;
        for (int i = 0; i < length; i++) {
            count += Long.bitCount(this.words[i] | otherWords[i]);
        }
        return count;
    }
}