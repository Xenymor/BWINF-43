package cc.retzlaff.timon.krocket;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static final int STEP_COUNT = 100;
    private static final int TEST_COUNT = 1_000;

    public static void main(String[] args) {
        String[] testInputLines = """
                2 7
                10 20 30 40
                12 31 11 8""".split("\n");
        Shot shot = findShot(testInputLines);
        System.out.println(shot);
        String[] numbers = testInputLines[0].split(" ");
        int radius = Integer.parseInt(numbers[1]);
        Gate[] gates = new Gate[Integer.parseInt(numbers[0])];
        parseGates(testInputLines, gates);
        JFrame myFrame = new MyFrame(shot, gates, radius);
        myFrame.setUndecorated(true);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setVisible(true);
    }

    private static Shot findShot(final String[] inputLines) {
        String[] numbers = inputLines[0].split(" ");
        int radius = Integer.parseInt(numbers[1]);
        Gate[] gates = new Gate[Integer.parseInt(numbers[0])];
        parseGates(inputLines, gates);
        if (gates.length == 0) {
            return null;
        } else if (gates.length == 1) {
            Gate gate = gates[0];
            if (radius < gate.getLength()) {
                return null;
            }
        }

        final Gate firstGate = gates[0];
        final Gate lastGate = gates[gates.length - 1];
        //TODO set STEP_COUNT to length * x
        final double stepSizeX1 = firstGate.getDiffX() / STEP_COUNT;
        final double stepSizeY1 = firstGate.getDiffY() / STEP_COUNT;
        final double stepSizeX2 = lastGate.getDiffX() / STEP_COUNT;
        final double stepSizeY2 = lastGate.getDiffY() / STEP_COUNT;

        for (int i = 0; i < STEP_COUNT; i++) {
            double x1 = firstGate.x1 + stepSizeX1 * i;
            double y1 = firstGate.y1 + stepSizeY1 * i;
            for (int j = 0; j < STEP_COUNT; j++) {
                double x2 = lastGate.x1 + stepSizeX2 * j;
                double y2 = lastGate.y1 + stepSizeY2 * j;
                Shot toTest = new Shot(x1, y1, x2, y2);
                if (testShot(toTest, gates, radius)) {
                    return toTest;
                }
            }
        }
        return null;
    }

    private static boolean testShot(final Shot shot, final Gate[] gates, final double radius) {
        int gateIndex = 0;
        Gate gate = gates[gateIndex];
        final double stepSizeX = shot.getDiffX() / TEST_COUNT;
        final double stepSizeY = shot.getDiffY() / TEST_COUNT;

        for (int i = 0; i < TEST_COUNT; i++) {
            final double x = shot.x1 + stepSizeX * i;
            final double y = shot.y1 + stepSizeY * i;

            if (gate.dist(x, y) <= 0.1) {
                if (gate.cornerDist(x, y) >= radius) {
                    gateIndex++;
                    if (gateIndex == gates.length) {
                        return true;
                    }
                    gate = gates[gateIndex];
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private static void parseGates(final String[] inputLines, final Gate[] gates) {
        for (int i = 0; i < gates.length; i++) {
            String[] values = inputLines[i + 1].split(" ");
            gates[i] = new Gate(Integer.parseInt(values[0]),
                    Integer.parseInt(values[1]),
                    Integer.parseInt(values[2]),
                    Integer.parseInt(values[3]));
        }
    }

    private static class MyFrame extends JFrame {
        final Shot shot;
        final Gate[] gates;
        final int radius;

        final double factor;

        public MyFrame(final Shot shot, final Gate[] gates, final int radius) {
            double max = Double.NEGATIVE_INFINITY;
            for (Gate gate : gates) {
                max = max(gate.x1, gate.x2, max);
                max = max(gate.y1, gate.y2, max);
            }

            setSize(1080, 1080);
            max += 10;
            factor = 1079 / max;

            this.shot = shot;
            this.gates = gates;
            this.radius = radius;
        }

        @Override
        public void paint(final Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            if (shot != null) {
                g.setColor(Color.RED);
                g2.setStroke(new BasicStroke((float) (radius * factor)));
                g.drawLine((int) (shot.x1 * factor), (int) (shot.y1 * factor), (int) (shot.x2 * factor), (int) (shot.y2 * factor));
            }
            g.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            for (Gate gate : gates) {
                g.drawLine((int) (gate.x1 * factor), (int) (gate.y1 * factor), (int) (gate.x2 * factor), (int) (gate.y2 * factor));
            }
        }

        public double max(double v1, double v2, double v3) {
            return Math.max(Math.max(v1, v2), v3);
        }
    }
}
