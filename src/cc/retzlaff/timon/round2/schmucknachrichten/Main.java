package cc.retzlaff.timon.round2.schmucknachrichten;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        //TODO relative path
        List<String> lines = Files.readAllLines(Path.of(args[0]));

        int colorCount = Integer.parseInt(lines.get(0));
        int[] colorSizes = new int[colorCount];
        String[] sizeStrings = lines.get(1).split(" ");
        for (int i = 0; i < colorSizes.length; i++) {
            colorSizes[i] = Integer.parseInt(sizeStrings[i]);
        }
        Arrays.sort(colorSizes);

        lines.remove(0);
        lines.remove(0);

        String msg = String.join("\n", lines);

        Map<Character, String> charTable = Encoder.generateTable(msg, colorSizes);

        StringBuilder builder = new StringBuilder();
        int length = 0;

        final char[] charArray = msg.toCharArray();
        for (final char c : charArray) {
            final String str = charTable.get(c);
            length += getLength(str, colorSizes);
            builder.append(str);
        }
        System.out.println("Encoded message: " + msg);
        System.out.println("As: " + builder);
        System.out.println("In " + builder.length() + " pearls (" + ((float) builder.length() / (msg.length() * 8) * 100) + "%) = " + length + "mm");

        final String encoded = builder.toString();
        System.out.println("Decoded as: " + Decoder.decode(encoded, charTable));
    }

    private static int getLength(final String str, final int[] colorSizes) {
        int length = 0;
        for (int i = 0; i < str.length(); i++) {
            length += colorSizes[Integer.parseInt(str.substring(i, i + 1))];
        }
        return length;
    }
}
