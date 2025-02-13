package cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Heuristics;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Labyrinths;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.State;

public class Max implements Heuristic {

    @Override
    public double getScore(final State pos, final Labyrinths labyrinths) {
        final int dist1 = labyrinths.getLabyrinth1().getDist(pos.x, pos.y, pos.jumpCount);
        final int dist2 = labyrinths.getLabyrinth2().getDist(pos.z, pos.w, pos.jumpCount);
        return Math.max(dist1, dist2);
    }

    @Override
    public String getName() {
        return "Max";
    }
}
