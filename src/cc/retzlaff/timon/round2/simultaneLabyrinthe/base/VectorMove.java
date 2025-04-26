package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;

public record VectorMove(State state,
                         Move move, int stepCount) {
    @Override
    public String toString() {
        return "VectorMove{" +
                "getState=" + state +
                ", move=" + move +
                ", getStepCount=" + stepCount +
                '}';
    }
}
