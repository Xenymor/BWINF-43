package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;

public record StateMove(State state,
                        Move move, int stepCount) {
    @Override
    public String toString() {
        return "StateMove{" +
                "getState=" + state +
                ", move=" + move +
                ", getStepCount=" + stepCount +
                '}';
    }
}
