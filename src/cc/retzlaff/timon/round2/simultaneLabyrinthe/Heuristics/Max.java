package cc.retzlaff.timon.round2.simultaneLabyrinthe.Heuristics;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.Labyrinths;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.Vector4;

public class Max implements Heuristic {

    @Override
    public double getScore(final Vector4 pos, final Labyrinths labyrinths) {
        final int dist1 = labyrinths.getLabyrinth1().getDist(pos.x, pos.y);
        final int dist2 = labyrinths.getLabyrinth2().getDist(pos.z, pos.w);
        return Math.max(dist1, dist2);
    }

    @Override
    public String getName() {
        return "Max";
    }
}
