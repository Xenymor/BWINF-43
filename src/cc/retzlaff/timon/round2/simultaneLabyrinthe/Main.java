package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final String inputFilePath = "C:\\Users\\timon\\Documents\\Programmieren\\Java\\BWINF-43\\src\\cc\\retzlaff\\timon\\round2\\simultaneLabyrinthe\\examples\\" +
            "labyrinthe1.txt";

    public static void main(String[] args) throws IOException {
        //TODO relative path
        List<String> input = Files.readAllLines(Path.of(inputFilePath));
        Labyrinths labyrinths = new Labyrinths(input);
        //labyrinths.draw(FIELD_SIZE);

        LabyrinthSolver solver = new LabyrinthSolver();
        List<Vector4> path = solver.solveSimultaneously(labyrinths);
        System.out.println(path.stream().map(Vector4::toString).collect(Collectors.joining("\n")));
        System.out.println("Length: " + path.size());
        labyrinths.drawSolution(getFieldSize(labyrinths), path);
    }

    private static int getFieldSize(final Labyrinths labyrinths) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenSize.getWidth() / (labyrinths.labyrinth1.width * labyrinths.labyrinthCount) < screenSize.getHeight() / labyrinths.labyrinth1.height) {
            return (int) (screenSize.getWidth() * 0.75 / labyrinths.labyrinth1.width);
        } else {
            return (int) (screenSize.getHeight() * 0.75 / labyrinths.labyrinth1.height);
        }
    }
}
