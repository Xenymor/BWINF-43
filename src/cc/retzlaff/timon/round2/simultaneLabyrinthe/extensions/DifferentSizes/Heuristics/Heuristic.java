package cc.retzlaff.timon.round2.simultaneLabyrinthe.extensions.DifferentSizes.Heuristics;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.extensions.DifferentSizes.Labyrinths;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.extensions.DifferentSizes.Vector4;

public interface Heuristic {
    double getScore(final Vector4 pos, final Labyrinths labyrinths);
    String getName();
}
