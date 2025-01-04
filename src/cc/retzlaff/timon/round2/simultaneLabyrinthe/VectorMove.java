package cc.retzlaff.timon.round2.simultaneLabyrinthe;

public record VectorMove(Vector4 vector,
                         Move move, int stepCount) {
    @Override
    public String toString() {
        return "VectorMove{" +
                "vector=" + vector +
                ", move=" + move +
                ", stepCount=" + stepCount +
                '}';
    }
}
