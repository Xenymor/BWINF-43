package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    private static final int FIELD_SIZE = 100;

    public static void main(String[] args) throws IOException {
        //TODO relative path
        List<String> input = Files.readAllLines(Path.of(args[0]));
        Labyrinth labyrinth = new Labyrinth(input);
        labyrinth.draw(0, FIELD_SIZE);

        LabyrinthSolver solver = new LabyrinthSolver();
        List<Vector2> path = solver.solve(labyrinth);
        labyrinth.drawSolution(0, FIELD_SIZE, path);
    }
}
