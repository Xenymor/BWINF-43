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
        lines.remove(1);

        String msg = String.join("\n", lines);

        Map<Character, Code> charTable = Encoder.generateTable(msg, colorCount);

        StringBuilder builder = new StringBuilder();

        final char[] charArray = msg.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            builder.append(charTable.get(charArray[i]).toString());
        }
        System.out.println("Encoded message: " + msg);
        System.out.println("As: " + builder);
        System.out.println("In " + builder.length() + " pearls");
    }
}
