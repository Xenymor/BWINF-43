package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;

import java.util.ArrayList;
import java.util.List;

public class Labyrinths {
    final Labyrinth labyrinth1;
    final Labyrinth labyrinth2;
    public final int labyrinthCount = 2;

    public final int startJumpCount;

    public Labyrinths(final List<String> input, final int jumpCount) {
        startJumpCount = jumpCount;
        labyrinth1 = new Labyrinth(input, jumpCount);
        int offset = 2 + labyrinth1.holeCount + labyrinth1.height - 1 + labyrinth1.height;
        if (offset > 1) {
            input.subList(1, offset).clear();
        }
        labyrinth2 = new Labyrinth(input, jumpCount);
    }

    public Labyrinth getLabyrinth1() {
        return labyrinth1;
    }

    public Labyrinth getLabyrinth2() {
        return labyrinth2;
    }

    public void draw(final int fieldSize) {
        labyrinth1.draw(0, fieldSize);
        labyrinth2.draw(labyrinth1.width * fieldSize + fieldSize / 2, fieldSize);
    }

    public State getStartPos() {
        return new State(labyrinth1.getStartPos(), labyrinth2.getStartPos(), startJumpCount);
    }

    public State getFinishPos() {
        final Vector3 finish1 = labyrinth1.getFinishPos();
        final Vector3 finish2 = labyrinth2.getFinishPos();
        return new State(finish1.x, finish1.y, finish2.x, finish2.y, 0);
    }

    final Move[] values = Move.values();

    public StateMove[] getPossibleFields(final State curr) {
        StateMove[] result = new StateMove[curr.jumpCount > 0 ? (values.length) : (values.length / 2)];
        for (int i = 0; i < result.length; i++) {
            final Move move = values[i];
            if (i > 3 && curr.jumpCount <= 0) {
                continue;
            }
            result[i] = new StateMove(
                    new State(
                            labyrinth1.getField(new Vector2(curr.x, curr.y), move),
                            labyrinth2.getField(new Vector2(curr.z, curr.w), move),
                            (i > 3) ? curr.jumpCount - 1 : curr.jumpCount
                    ),
                    move, 0);
        }
        return result;
    }

    public void drawSolution(final int fieldSize, final List<PositionData> path) {
        List<Move> path1 = new ArrayList<>(path.size());

        for (PositionData curr : path) {
            path1.add(curr.move);
        }

        labyrinth1.drawSolution(0, fieldSize, path1);
        labyrinth2.drawSolution(labyrinth1.width * fieldSize + fieldSize / 2, fieldSize, path1);
    }

    public void generateDists() {
        labyrinth1.generateDists();
        labyrinth2.generateDists();
    }

}
