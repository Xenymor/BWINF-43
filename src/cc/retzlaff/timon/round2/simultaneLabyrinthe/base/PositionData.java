package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;

import java.util.Objects;

public final class PositionData {
    private final State state;
    private final double score;
    final int stepCount;
    final PositionData previous;
    final Move move;

    public PositionData(State state, double score, int stepCount, PositionData previous, Move move) {
        this.state = state;
        this.score = score;
        this.stepCount = stepCount;
        this.previous = previous;
        this.move = move;
    }

    @Override
    public String toString() {
        return "PositionData{" +
                "vector=" + state +
                ", score=" + score +
                ", stepCount=" + stepCount +
                ", previous=" + previous +
                '}';
    }

    public State getState() {
        return state;
    }

    public double getScore() {
        return score;
    }

    public int getStepCount() {
        return stepCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PositionData) obj;
        return Objects.equals(this.state, that.state) &&
                Double.doubleToLongBits(this.score) == Double.doubleToLongBits(that.score) &&
                this.stepCount == that.stepCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, score, stepCount);
    }

}
