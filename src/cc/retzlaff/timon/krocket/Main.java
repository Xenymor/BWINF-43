package cc.retzlaff.timon.krocket;

public class Main {
    public static void main(String[] args) {
        String[] testInputLines = """
                2 7
                10 20 30 40
                12 31 11 8""".split("\n");
        Shot shot = findShot(testInputLines);
    }

    private static Shot findShot(final String[] inputLines) {
        String[] numbers = inputLines[0].split(" ");
        Gate[] gates = new Gate[Integer.parseInt(numbers[0])];
        int r = Integer.parseInt(numbers[1]);
        for (int i = 0; i < gates.length; i++) {
            String[] values = inputLines[i + 1].split(" ");
            gates[i] = new Gate(Integer.parseInt(values[0]),
                    Integer.parseInt(values[1]),
                    Integer.parseInt(values[2]),
                    Integer.parseInt(values[3]));
        }
        if (gates.length == 0) {
            return null;
        } else if (gates.length == 1) {
            Gate gate = gates[0];
            if (r < gate.getLength()) {
                return gate.
            }
        }
        for (int x = gates[0].x1; x < gates[0].x2; x++) {

        }
    }
}
