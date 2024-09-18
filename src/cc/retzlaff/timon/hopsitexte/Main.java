package cc.retzlaff.timon.hopsitexte;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String input = """
                Bela und Amira ist im Deutschunterricht oft lang-
                weilig. Daher haben sie sich ein neues Spiel
                ausgedacht: Texthopsen. Sie suchen sich einen Text
                im Schulbuch aus und hopsen darin um die Wette.
                Das geht so: Jeder Buchstabe hat eine Sprungweite
                zugeordnet (siehe Tabelle). Bela fängt beim ersten,
                Amira beim zweiten Buchstaben des Textes an.
                Von dieser Startposition springen sie jeweils so
                viele Positionen weiter, wie die Sprungweite des
                Buchstabens vorgibt. Alles was kein Buchstabe
                ist, überspringen sie dabei einfach. An der neuen
                Position lesen sie dann jeweils den nächsten
                Buchstaben mit einer neuen Sprungweite. Das
                Hopsen wiederholen sie abwechselnd so lange,
                bis sie aus dem Text herausspringen. Wer dies als
                Erstes schafft, hat gewonnen
                """;
        char[] inputChars = input.toLowerCase().toCharArray();
        List<Integer> originalIndex = new ArrayList<>();
        StringBuilder formattedTextBuilder = new StringBuilder();
        for (int i = 0; i < inputChars.length; i++) {
            final char currChar = inputChars[i];
            if (Character.isAlphabetic(currChar)) {
                formattedTextBuilder.append(currChar);
                originalIndex.add(i);
            }
        }
        String formattedText = formattedTextBuilder.toString();
        System.out.println(formattedText);
        List<Integer> positions1 = getPositions(formattedText, 0);
        List<Integer> positions2 = getPositions(formattedText, 1);
        final boolean isHopsiText = !(positions1.get(positions1.size() - 1).equals(positions2.get(positions2.size() - 1)));
        System.out.println(isHopsiText ? "Dies ist ein Hopsitext" : "Dies ist kein Hopsitext");
        if (!isHopsiText) {
            for (int i = 0; i < positions1.size(); i++) {
                final Integer position = positions1.get(i);
                if (positions2.contains(position)) {
                    int ogPosition = originalIndex.get(position);
                    try {
                        int ogLastPos1 = originalIndex.get(positions1.get(i - 1));
                        int ogLastPos2 = originalIndex.get(positions2.get(positions2.indexOf(position) - 1));
                        System.out.println("The guys collide at " + ogPosition + " after jumping from " + ogLastPos1 + " and " + ogLastPos2);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("The guys collide at " + ogPosition);
                    }
                    break;
                }
            }
        }
    }

    private static List<Integer> getPositions(final String text, final int startPosition) {
        List<Integer> result = new ArrayList<>();
        char[] chars = text.toCharArray();
        int position = startPosition;
        while (position < chars.length) {
            result.add(position);
            position += getValue(chars[position]);
        }
        return result;
    }

    private static int getValue(final char letter) {
        if (letter < 128) {
            return letter - 96;
        } else {
            switch (letter) {
                case 'ä' -> {
                    return 27;
                }
                case 'ö' -> {
                    return 28;
                }
                case 'ü' -> {
                    return 29;
                }
                case 'ß' -> {
                    return 30;
                }
                default -> throw new IllegalArgumentException(letter + " is no lowercase letter");
            }
        }
    }

    private static String formatInput(final String input) {
        return input.toLowerCase().replaceAll("[^a-zäöüß]", "");
    }
}
