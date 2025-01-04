package cc.retzlaff.timon.round2.simultaneLabyrinthe;

public record VectorScore(Vector4 vector, double score, int stepCount) {
    @Override
    public String toString() {
        return "VectorScore{" +
                "vector=" + vector +
                ", score=" + score +
                ", stepCount=" + stepCount +
                '}';
    }
}
