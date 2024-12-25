package cc.retzlaff.timon.round2.schmucknachrichten;

import java.util.HashMap;
import java.util.Map;

public class Encoder {
    public static Map<Character, Code> generateTable(final String msg, final int colorCount) {
        Map<Character, Integer> counts = new HashMap<>();
        for (char curr : msg.toCharArray()) {
            if (!counts.containsKey(curr)) {
                counts.put(curr, 0);
            } else {
                counts.put(curr, counts.get(curr) + 1);
            }
        }
        
    }
}
