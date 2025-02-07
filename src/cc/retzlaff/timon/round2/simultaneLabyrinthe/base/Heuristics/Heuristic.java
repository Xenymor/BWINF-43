package cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Heuristics;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Labyrinths;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Vector4;

public interface Heuristic {
    double getScore(final Vector4 pos, final Labyrinths labyrinths);
    String getName();
}
