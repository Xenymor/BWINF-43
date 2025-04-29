package cc.retzlaff.timon.round2.schmucknachrichten.base;

import java.util.HashMap;
import java.util.Map;

public class Decoder {
    public static String decode(final String msg, final Map<Character, String> charTable) {
        final Map<String, Character> table = new HashMap<>(charTable.size());
        for (Character character : charTable.keySet()) {
            table.put(charTable.get(character), character);
        }

        StringBuilder result = new StringBuilder();
        int lastIndex = 0;
        for (int i = 0; i <= msg.length(); i++) {
            String substring = msg.substring(lastIndex, i);
            if (table.containsKey(substring)) {
                lastIndex = i;
                result.append(table.get(substring));
            }
        }
        return result.toString();
    }
}
