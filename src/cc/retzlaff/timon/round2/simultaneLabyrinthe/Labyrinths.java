package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import java.util.ArrayList;
import java.util.List;

public class Labyrinths {
    final Labyrinth labyrinth1;
    final Labyrinth labyrinth2;
    public final int labyrinthCount = 2;

    public Labyrinths(final List<String> input) {
        labyrinth1 = new Labyrinth(input);
        int offset = 2 + labyrinth1.holeCount + labyrinth1.height - 1 + labyrinth1.height;
        if (offset > 1) {
            input.subList(1, offset).clear();
        }
        labyrinth2 = new Labyrinth(input);
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

    public Vector4 getStartPos() {
        return new Vector4(labyrinth1.getStartPos(), labyrinth2.getStartPos());
    }

    public Vector4 getFinishPos() {
        return new Vector4(labyrinth1.getFinishPos(), labyrinth2.getFinishPos());
    }

    Move[] values = Move.values();

    public VectorMove[] getPossibleFields(final Vector4 curr) {
        VectorMove[] result = new VectorMove[values.length];
        for (int i = 0; i < result.length; i++) {
            final Move move = values[i];
            result[i] = new VectorMove(new Vector4(labyrinth1.getField(new Vector2(curr.x, curr.y), move), labyrinth2.getField(new Vector2(curr.z, curr.w), move)), move, 0);
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
