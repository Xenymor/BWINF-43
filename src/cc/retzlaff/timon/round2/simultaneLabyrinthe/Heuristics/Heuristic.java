package cc.retzlaff.timon.round2.simultaneLabyrinthe.Heuristics;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.Labyrinths;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.Vector4;

public interface Heuristic {
    double getScore(final Vector4 pos, final Labyrinths labyrinths);
}
