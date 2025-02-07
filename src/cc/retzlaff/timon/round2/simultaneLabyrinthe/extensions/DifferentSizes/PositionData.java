package cc.retzlaff.timon.round2.simultaneLabyrinthe.extensions.DifferentSizes;

import java.util.Objects;

public final class PositionData {
    private final Vector4 vector;
    private final double score;
    final int stepCount;
    final PositionData previous;
    final Move move;

    public PositionData(Vector4 vector, double score, int stepCount, PositionData previous, Move move) {
        this.vector = vector;
        this.score = score;
        this.stepCount = stepCount;
        this.previous = previous;
        this.move = move;
    }

    @Override
    public String toString() {
        return "PositionData{" +
                "vector=" + vector +
                ", score=" + score +
                ", stepCount=" + stepCount +
                ", previous=" + previous +
                '}';
    }

    public Vector4 getVector() {
        return vector;
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
        return Objects.equals(this.vector, that.vector) &&
                Double.doubleToLongBits(this.score) == Double.doubleToLongBits(that.score) &&
                this.stepCount == that.stepCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vector, score, stepCount);
    }

}
