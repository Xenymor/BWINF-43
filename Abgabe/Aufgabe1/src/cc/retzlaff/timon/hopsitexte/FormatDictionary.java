package cc.retzlaff.timon.round1.hopsitexte;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FormatDictionary {

    public static final String PATH = "C:\\Users\\timon\\Documents\\Programmieren\\Java\\BWINF-43\\src\\cc\\retzlaff\\timon\\hopsitexte\\openthesaurus.txt";
    public static final String PATH_2 = "C:\\Users\\timon\\Documents\\Programmieren\\Java\\BWINF-43\\src\\cc\\retzlaff\\timon\\hopsitexte\\synonyms.csv";

    public static void main(String[] args) throws IOException {
        StringBuilder builder = new StringBuilder();
        List<String> lines = Files.readAllLines(Path.of(PATH));
        HashMap<String, List<String>> synonyms = new HashMap<>();
        for (final String line : lines) {
            String[] words = formatLine(line, builder).split(";");
            for (int j = 0; j < words.length; j++) {
                final String word = words[j];
                if (word.isEmpty()) {
                    continue;
                }
                final boolean containsKey = synonyms.containsKey(word);
                List<String> currSynonyms = containsKey ? synonyms.get(word) : new ArrayList<>();
                for (int k = 0; k < words.length; k++) {
                    if (j != k) {
                        final String word1 = words[k];
                        if (word1.isEmpty()) {
                            continue;
                        }
                        currSynonyms.add(word1);
                    }
                }
                if (!containsKey) {
                    synonyms.put(word, currSynonyms);
                }
            }
        }
        Set<String> keys = synonyms.keySet();
        final Path path2 = Path.of(PATH_2);
        Files.deleteIfExists(path2);
        Files.createFile(path2);
        BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_2));
        for (String key : keys) {
            builder.append(key);
            final List<String> strings = synonyms.get(key);
            for (String synonym : strings) {
                builder.append(",").append(synonym);
            }
            builder.append("\n");
        }
        final String result = builder.toString().replaceAll(" {2,}", " ")
                .replaceAll("[ ,]*\n[ ,]*", "\n")
                .replaceAll(" *, *", ",");
        writer.append(result);
        writer.close();
    }

    private static String formatLine(final String line, final StringBuilder builder) {
        char[] chars = line.toCharArray();
        boolean add = true;
        char last = '^';
        for (char curr : chars) {
            if (curr == '(') {
                add = false;
            } else if (curr == ')') {
                add = true;
                continue;
            }
            if (add) {
                if (!(last == ' ' && ' ' == curr)) {
                    builder.append(curr);
                }
            }
            last = curr;
        }
        final String result = builder.toString();
        builder.delete(0, builder.length());
        return result;
    }
}
