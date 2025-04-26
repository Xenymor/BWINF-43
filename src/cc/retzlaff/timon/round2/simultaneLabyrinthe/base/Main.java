package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;

import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Heuristics.Heuristic;
import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Heuristics.WeightedAverage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static final double SCREEN_PERCENTAGE = 0.75;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Please provide the path to the input file as an argument.");
        }
        List<String> input = Files.readAllLines(Path.of(args[0]));
        int jumpCount;
        try {
            jumpCount = Integer.parseInt(input.get(0));
            input.remove(0);
        } catch (NumberFormatException e) {
            jumpCount = 0;
        }

        Labyrinths labyrinths = new Labyrinths(input, jumpCount);
        labyrinths.draw(getFieldSize(labyrinths));

        LabyrinthSolver solver = new LabyrinthSolver();
        long startTime = System.nanoTime();
        final Heuristic heuristic = new WeightedAverage(0.99999);
        List<PositionData> path = solver.solveSimultaneously(labyrinths, heuristic);
        System.out.println("Time needed: " + (System.nanoTime() - startTime) / 1_000_000_000f + "s");
        System.out.println("Using heuristic: " + heuristic.getName());
        /*
        4:
            Time needed: 26.0843s
            Using heuristic: WeightedAverage: 0.999-0.001
            Length: 14384
        5:
            Time needed: 104.48041s
            Using heuristic: WeightedAverage: 0.999-0.001
            Length: 1308
        6:
            Time needed: 0.4779969s
            Using heuristic: WeightedAverage: 0.999-0.001
            Length: 1844
        7:
            Time needed: 0.0018915s
            Using heuristic: WeightedAverage: 0.999-0.001
            Length: -1
        8:
            Time needed: 74.77536s
            Using heuristic: WeightedAverage: 0.999-0.001
            Length: 472
        9:
            Time needed: 54.312172s
            Using heuristic: WeightedAverage: 0.999-0.001
            Length: 1012
        Jump-labyrinthe4.txt:
            Time needed: 10.888596s
            Using heuristic: WeightedAverage: 0.99999-1.0E-5
            Length: 1834
        */
        /*for (int i = 0; i < path.size() - 1; i++) {
            System.out.println(path.get(i).move + path.get(i).getState().toString());
        }*/
        System.out.println("Length: " + (path.size() - 1));
        labyrinths.drawSolution(getFieldSize(labyrinths), path);
    }

    private static int getFieldSize(final Labyrinths labyrinths) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final double fieldWidth = labyrinths.labyrinth1.width * labyrinths.labyrinthCount + 0.5;
        if (screenSize.getWidth() / fieldWidth < screenSize.getHeight() / labyrinths.labyrinth1.height) {
            return (int) (screenSize.getWidth() * SCREEN_PERCENTAGE / fieldWidth);
        } else {
            return (int) (screenSize.getHeight() * SCREEN_PERCENTAGE / labyrinths.labyrinth1.height);
        }
    }
}
