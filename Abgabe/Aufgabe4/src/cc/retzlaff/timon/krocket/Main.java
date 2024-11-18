package cc.retzlaff.timon.krocket;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final int STEP_COUNT = 10_000;

    public static void main(String[] args) throws IOException {
        java.util.List<String> testInputLines = Files.readAllLines(Path.of(args[0]));
        Shot shot = findShot(testInputLines);
        System.out.println(shot);
        String[] numbers = testInputLines.get(0).split(" ");
        int radius = Integer.parseInt(numbers[1]);
        Gate[] gates = new Gate[Integer.parseInt(numbers[0])];
        parseGates(testInputLines, gates);
        JFrame myFrame = new MyFrame(shot, gates, radius);
        myFrame.setUndecorated(true);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setVisible(true);
    }

    private static Shot findShot(final java.util.List<String> inputLines) {
        String[] numbers = inputLines.get(0).split(" ");
        int radius = Integer.parseInt(numbers[1]);
        Gate[] gates = new Gate[Integer.parseInt(numbers[0])];
        parseGates(inputLines, gates);
        if (gates.length == 0) {
            return null;
        } else if (gates.length == 1) {
            Gate gate = gates[0];
            if (2 * radius > gate.getLength()) {
                return null;
            } else {
                double u1 = gate.getDiffX();
                double u2 = gate.getDiffY();
                double n2 = -u1 / u2;
                double dx = radius / Math.sqrt(1 + n2 * n2);
                double dy = (radius * n2) / Math.sqrt(1 + n2 * n2);

                double gateMidX = gate.x1 + u1 / 2;
                double gateMidY = gate.y1 + u1 / 2;
                return new Shot(gateMidX + dx, gateMidY + dy, gateMidX, gateMidY);
            }
        }

        final Gate firstGate = gates[0];
        final Gate lastGate = gates[gates.length - 1];
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
        double lastS = -0.5;
        for (Gate gate : gates) {
            double newS = getParam(shot, gate);
            if (newS < lastS || !isLegit(newS, shot, gate, radius, true)) {
                return false;
            } else {
                lastS = newS;
            }
        }
        return true;
    }

    private static boolean isLegit(final double s, final Shot shot, final Gate gate, final double radius, boolean checkRadius) {
        final double x = shot.getDiffX() * s + shot.x1;
        final double y = shot.getDiffY() * s + shot.y1;

        if (x <= Math.max(gate.x1, gate.x2) && x >= Math.min(gate.x1, gate.x2)
                && y <= Math.max(gate.y1, gate.y2) && y >= Math.min(gate.y1, gate.y2)) {
            return !checkRadius || checkRadius(shot, gate, radius);
        } else {
            return false;
        }
    }

    private static boolean checkRadius(final Shot shot, final Gate gate, final double radius) {
        double u1 = shot.getDiffX();
        double u2 = shot.getDiffY();
        double n2 = -u1 / u2;
        double dx = radius / Math.sqrt(1 + n2 * n2);
        double dy = (radius * n2) / Math.sqrt(1 + n2 * n2);

        final Shot test1 = new Shot(shot.x1 + dx, shot.y1 + dy, shot.x2 + dx, shot.y2 + dy);
        final double s1 = getParam(test1, gate);
        final Shot test2 = new Shot(shot.x1 - dx, shot.y1 - dy, shot.x2 - dx, shot.y2 - dy);
        final double s2 = getParam(test2, gate);
        return isLegit(s1, test1, gate, radius, false)
                && isLegit(s2, test2, gate, radius, false);
    }

    private static double getParam(final Shot shot, final Gate gate) {
        final double a1 = shot.x1;
        final double a2 = shot.y1;
        final double u1 = shot.getDiffX();
        final double u2 = shot.getDiffY();

        final double b1 = gate.x1;
        final double b2 = gate.y1;
        final double v1 = gate.getDiffX();
        final double v2 = gate.getDiffY();

        if (u1 == 0 || u2 == 0 || v1 == 0 || v2 == 0) {
            if ((u1 == v1 && u1 == 0) || (u2 == v2 && u2 == 0)) {
                return -1;
            }
        } else {
            if (u1 / u2 == v1 / v2 || u1 / u2 == -v1 / v2) {
                return -1;
            }
        }

        if (v1 == 0) {
            if (v2 == 0) {
                return -1;
            } else {
                return (b1 - a1) / u1;
            }
        } else {
            final double p1 = a2 - b2 - (a1 * v2) / v1 + (b1 * v2) / v1;
            final double p2 = (u1 * v2) / v1 - u2;
            return p1 / p2;
        }
    }

    private static void parseGates(final java.util.List<String> inputLines, final Gate[] gates) {
        for (int i = 0; i < gates.length; i++) {
            String[] values = inputLines.get(i + 1).split(" ");
            gates[i] = new Gate(
                    Integer.parseInt(values[0]),
                    Integer.parseInt(values[1]),
                    Integer.parseInt(values[2]),
                    Integer.parseInt(values[3]));
        }
    }

    private static class MyFrame extends JFrame {
        final Shot shot;
        final Gate[] gates;
        final int radius;

        final double factorX;
        final double factorY;
        final double xOffset;
        final double yOffset;
        private final int screenSize = 1000;

        public MyFrame(final Shot shot, final Gate[] gates, final int radius) {
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            for (Gate gate : gates) {
                maxX = max(gate.x1, gate.x2, maxX);
                maxY = max(gate.y1, gate.y2, maxY);
                minX = min(gate.x1, gate.x2, minX);
                minY = min(gate.y1, gate.y2, minY);
            }

            setSize(screenSize, screenSize);
            maxX += 20;
            factorX = (screenSize - 1) / (maxX - minX);

            maxY += 20;
            factorY = (screenSize - 1) / (maxY - minY);

            xOffset = -minX + 10;
            yOffset = -minY + 10;

            this.shot = shot;
            this.gates = gates;
            this.radius = radius;
        }

        private double min(final double v1, final double v2, final double v3) {
            return Math.min(Math.min(v1, v2), v3);
        }

        @Override
        public void paint(final Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            if (shot != null) {
                g.setColor(Color.RED);
                g2.setStroke(new BasicStroke(Math.max((int) (radius * 2 * Math.min(factorX, factorY)), 2)));
                g.drawLine(xCoordToScreen(shot.x1), yCoordToScreen(shot.y1), xCoordToScreen(shot.x2), yCoordToScreen(shot.y2));
            }
            g.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            for (final Gate gate : gates) {
                g.drawLine(xCoordToScreen(gate.x1), yCoordToScreen(gate.y1), xCoordToScreen(gate.x2), yCoordToScreen(gate.y2));
            }
        }

        private int yCoordToScreen(final double y1) {
            return screenSize - (int) (y1 * factorY + yOffset);
        }

        private int xCoordToScreen(final double x2) {
            return (int) (x2 * factorX + xOffset);
        }

        public double max(double v1, double v2, double v3) {
            return Math.max(Math.max(v1, v2), v3);
        }
    }
}
