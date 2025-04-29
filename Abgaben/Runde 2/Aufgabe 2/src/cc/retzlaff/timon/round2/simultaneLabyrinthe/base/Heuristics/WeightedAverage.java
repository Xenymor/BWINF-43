package cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Heuristics;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Labyrinths;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.State;

public class WeightedAverage implements Heuristic {
    final double higherWeight;
    final double lowerWeight;

    public WeightedAverage(final double higherWeight) {
        this.higherWeight = higherWeight;
        lowerWeight = 1 - higherWeight;
    }

    public double getScore(final State pos, final Labyrinths labyrinths) {
        final int dist1 = labyrinths.getLabyrinth1().getDist(pos.x, pos.y, pos.jumpCount);
        final int dist2 = labyrinths.getLabyrinth2().getDist(pos.z, pos.w, pos.jumpCount);
        return Math.max(dist2, dist1) * higherWeight + Math.min(dist2, dist1) * lowerWeight;
    }

    @Override
    public String getName() {
        return "WeightedAverage: " + Math.round(higherWeight * 100000) / 100000d + "-" + Math.round(lowerWeight * 100000) / 100000d;
    }
}
