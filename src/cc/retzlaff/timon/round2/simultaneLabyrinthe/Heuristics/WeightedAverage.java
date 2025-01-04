package cc.retzlaff.timon.round2.simultaneLabyrinthe.Heuristics;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.Labyrinths;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.Vector4;

public class WeightedAverage implements Heuristic{
    public double getScore(final Vector4 pos, final Labyrinths labyrinths) {
        final int dist1 = labyrinths.getLabyrinth1().getDist(pos.x, pos.y);
        final int dist2 = labyrinths.getLabyrinth2().getDist(pos.z, pos.w);
        return Math.max(dist2, dist1) * 0.99 + Math.min(dist2, dist1) * 0.01;
    }
}
