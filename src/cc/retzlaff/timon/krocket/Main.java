package cc.retzlaff.timon.krocket;

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
        int gateIndex = 1;
        Gate gate = gates[gateIndex];
        for (double x = shot.x1; x < shot.x2; x += shot.getDiffX() / TEST_COUNT) {
            for (double y = shot.y1; y < shot.y2; y += shot.getDiffY() / TEST_COUNT) {
                if (gate.dist(x, y) <= radius) {
                    if (gate.cornerDist(x, y) >= radius) {
                        gateIndex++;
                        if (gateIndex == gates.length - 1) {
                            return true;
                        }
                    } else {
                        return false;
                    }
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
}
