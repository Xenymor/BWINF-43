package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final int FIELD_SIZE = 100;

    public static void main(String[] args) throws IOException {
        //TODO relative path
        List<String> input = Files.readAllLines(Path.of(args[0]));
        Labyrinths labyrinths = new Labyrinths(input);
        labyrinths.draw(FIELD_SIZE);

        LabyrinthSolver solver = new LabyrinthSolver();
        List<Vector4> path = solver.solveSimultaneously(labyrinths);
        System.out.println(path.stream().map(Vector4::toString).collect(Collectors.joining("\n")));
        labyrinths.drawSolution(FIELD_SIZE, path);
    }
}
