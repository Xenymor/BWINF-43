package cc.retzlaff.timon.round2.simultaneLabyrinthe.Heuristics;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.Labyrinths;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.Vector4;

public class WeightedAverage implements Heuristic {
    final double higherWeight;
    final double lowerWeight;

    public WeightedAverage(final double higherWeight) {
        this.higherWeight = higherWeight;
        lowerWeight = 1 - higherWeight;
    }

    public double getScore(final Vector4 pos, final Labyrinths labyrinths) {
        final int dist1 = labyrinths.getLabyrinth1().getDist(pos.x, pos.y);
        final int dist2 = labyrinths.getLabyrinth2().getDist(pos.z, pos.w);
        return Math.max(dist2, dist1) * higherWeight + Math.min(dist2, dist1) * lowerWeight;
    }

    @Override
    public String getName() {
        return "WeightedAverage: " + Math.round(higherWeight * 1000) / 1000d + "-" + Math.round(lowerWeight * 1000) / 1000d;
    }
}
