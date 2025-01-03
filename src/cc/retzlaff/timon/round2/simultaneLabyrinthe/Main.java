package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    private static final String inputFilePath = "C:\\Users\\timon\\Documents\\Programmieren\\Java\\BWINF-43\\src\\cc\\retzlaff\\timon\\round2\\simultaneLabyrinthe\\examples\\" +
            "labyrinthe4.txt";

    public static void main(String[] args) throws IOException {
        //TODO relative path in args
        List<String> input = Files.readAllLines(Path.of(inputFilePath));
        Labyrinths labyrinths = new Labyrinths(input);

        LabyrinthSolver solver = new LabyrinthSolver();
        long startTime = System.nanoTime();
        List<VectorMove> path = solver.solveSimultaneously(labyrinths);
        System.out.println("Time needed: " + (System.nanoTime() - startTime) / 1_000_000_000f + "s");
        /*for (int i = 0; i < path.size() - 1; i++) {
            System.out.println(path.get(i).move());
        }*/
        System.out.println("Length: " + path.size());
        labyrinths.drawSolution(getFieldSize(labyrinths), path);
    }

    private static int getFieldSize(final Labyrinths labyrinths) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final double fieldWidth = labyrinths.labyrinth1.width * labyrinths.labyrinthCount + 0.5;
        if (screenSize.getWidth() / fieldWidth < screenSize.getHeight() / labyrinths.labyrinth1.height) {
            return (int) (screenSize.getWidth() * 0.75 / fieldWidth);
        } else {
            return (int) (screenSize.getHeight() * 0.75 / labyrinths.labyrinth1.height);
        }
    }
}
