package cc.retzlaff.timon.round2.schmucknachrichten.extension;

import java.util.HashMap;
import java.util.Map;

public class Decoder {
    public static String decode(final String msg, final Map<String, String> charTable) {
        final Map<String, String> table = new HashMap<>(charTable.size());
        for (String character : charTable.keySet()) {
            table.put(charTable.get(character), character);
        }

        String marker = charTable.get("marker");
        //Check where the marker is
        int markerIndex = msg.indexOf(marker);
        if (markerIndex == -1) {
            for (int i = 1; i <= marker.length(); i++) {
                if (marker.substring(0, i).equals(msg.substring(msg.length() - i))
                        && marker.substring(i).equals(msg.substring(0, marker.length() - i))) {
                    markerIndex = msg.length() - i;
                }
            }
        }
        if (markerIndex == -1) {
            throw new IllegalArgumentException("Marker not found in message");
        }
        int startIndex = (markerIndex + marker.length()) % msg.length();
        StringBuilder result = new StringBuilder();
        int lastIndex = 0;
        for (int i = 0; i <= msg.length() - marker.length(); i++) {
            final int endIndex = (i + startIndex) % msg.length();
            String substring = msg.substring((lastIndex + startIndex) % msg.length(), endIndex == 0 ? msg.length() : endIndex);
            if (table.containsKey(substring)) {
                lastIndex = i;
                result.append(table.get(substring));
            }
        }
        return result.toString();
    }
}
