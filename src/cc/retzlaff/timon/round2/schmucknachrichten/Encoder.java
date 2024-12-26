package cc.retzlaff.timon.round2.schmucknachrichten;

import java.util.*;

public class Encoder {
    public static Map<Character, String> generateTable(final String msg, final Integer[] colorIndices) {
        Map<Character, Integer> counts = new HashMap<>();
        final char[] chars = msg.toCharArray();
        for (char curr : chars) {
            if (!counts.containsKey(curr)) {
                counts.put(curr, 1);
            } else {
                counts.put(curr, counts.get(curr) + 1);
            }
        }

        List<Node> rootNodes = generateTree(counts, colorIndices.length);

        for (int i = 0; i < rootNodes.size(); i++) {
            rootNodes.get(i).assignCodesRecursively("", colorIndices[i], colorIndices);
        }

        return getTable(rootNodes);
    }

    private static Map<Character, String> getTable(final List<Node> rootNodes) {
        Map<Character, String> result = new HashMap<>();
        for (Node node : rootNodes) {
            fillTableRecursively(node, result);
        }
        return result;
    }

    private static void fillTableRecursively(final Node node, final Map<Character, String> result) {
        if (node.isLeaf) {
            result.put(node.getChar(), node.getCode());
        } else {
            for (Node child : node.children) {
                fillTableRecursively(child, result);
            }
        }
    }

    private static List<Node> generateTree(final Map<Character, Integer> counts, final int colorCount) {
        List<Node> nodes = new ArrayList<>();

        for (Character curr : counts.keySet()) {
            nodes.add(new Node(curr, counts.get(curr), colorCount, true));
        }

        while (nodes.size() > colorCount) {
            joinLowest(nodes, colorCount);
        }

        return nodes;
    }

    private static void joinLowest(final List<Node> nodes, final int colorCount) {
        int[] lowestIndices = new int[colorCount];
        int[] lowestValues = new int[colorCount];
        Arrays.fill(lowestValues, Integer.MAX_VALUE);

        //TODO add sorting
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);
            addValue(node, i, lowestIndices, lowestValues);
        }

        List<Node> lowest = new ArrayList<>(lowestIndices.length);
        int value = 0;
        for (int i = 0; i < lowestIndices.length; i++) {
            final int lowestIndex = lowestIndices[i];
            lowest.add(nodes.get(lowestIndex));
            value += lowestValues[i];
        }
        Node newNode = new Node(lowest, value, colorCount, false);
        Arrays.sort(lowestIndices);
        for (int i = lowestIndices.length - 1; i >= 0; i--) {
            nodes.remove(lowestIndices[i]);
        }
        nodes.add(newNode);
    }

    private static void addValue(final Node node, final int i, final int[] lowestIndices, final int[] lowestValues) {
        int value = node.value;
        for (int j = 0; j < lowestValues.length; j++) {
            if (value < lowestValues[j]) {
                insert(i, lowestIndices, j);
                insert(value, lowestValues, j);
                return;
            }
        }
    }

    private static void insert(final int value, final int[] arr, final int index) {
        int buff = value;
        int buff2;
        for (int i = index; i < arr.length; i++) {
            buff2 = arr[i];
            arr[i] = buff;
            buff = buff2;
        }
    }
}
