package cc.retzlaff.timon.round2.schmucknachrichten;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        //TODO relative path
        List<String> lines = Files.readAllLines(Path.of(args[0]));

        int colorCount = Integer.parseInt(lines.get(0));
        //TODO get colorSizes
        lines.remove(0);
        lines.remove(0);

        String msg = String.join("\n", lines);

        Map<Character, String> charTable = Encoder.generateTable(msg, colorCount);

        StringBuilder builder = new StringBuilder();

        final char[] charArray = msg.toCharArray();
        for (final char c : charArray) {
            builder.append(charTable.get(c));
        }
        System.out.println("Encoded message: " + msg);
        System.out.println("As: " + builder);
        System.out.println("In " + builder.length() + " pearls");
    }
}
