package cc.retzlaff.timon.round2.simultaneLabyrinthe.extensions.DifferentSizes;

public record VectorMove(Vector4 vector,
                         Move move, int stepCount) {
    @Override
    public String toString() {
        return "VectorMove{" +
                "getState=" + vector +
                ", move=" + move +
                ", getStepCount=" + stepCount +
                '}';
    }
}
