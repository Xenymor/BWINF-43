package cc.retzlaff.timon.round2.schmucknachrichten;

public class Signature implements Comparable<Signature> {
    final int[] signature;
    final int[] partialSums;
    double cost;
    Signature previous;

    public Signature(final int[] signature, final double cost, final Signature previous) {
        this.signature = signature;
        this.cost = cost;
        this.previous = previous;
        partialSums = new int[signature.length];
        fillPartialSums();
    }

    private void fillPartialSums() {
        partialSums[0] = signature[0];
        for (int i = 1; i < partialSums.length; i++) {
            partialSums[i] = partialSums[i - 1] + signature[i];
        }
    }

    public void changePath(Signature previous, double cost) {
        this.previous = previous;
        this.cost = cost;
    }


    @Override
    public int compareTo(final Signature o) {
        for (int i = signature.length - 1; i >= 0; i--) {
            final int result = Integer.compare(partialSums[i], o.partialSums[i]);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
