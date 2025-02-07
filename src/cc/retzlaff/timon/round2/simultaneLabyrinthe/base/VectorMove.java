package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;

public record VectorMove(Vector4 vector,
                         Move move, int stepCount) {
    @Override
    public String toString() {
        return "VectorMove{" +
                "getVector=" + vector +
                ", move=" + move +
                ", getStepCount=" + stepCount +
                '}';
    }
}
