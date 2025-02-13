package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;

public record VectorMove(State vector,
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
