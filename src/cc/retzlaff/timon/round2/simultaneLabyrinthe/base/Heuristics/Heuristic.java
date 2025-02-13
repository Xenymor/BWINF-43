package cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Heuristics;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Labyrinths;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.State;

public interface Heuristic {
    double getScore(final State pos, final Labyrinths labyrinths);
    String getName();
}
