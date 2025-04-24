package cc.retzlaff.timon.round2.schmucknachrichten;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Encoder {
    private static final int OPTIMIZATION_STEPS = 200;

    public static Map<Character, String> generateTable(final String msg, final int[] costs) {
        final MapInt mapInt = getCounts(msg);
        Map<Character, AtomicInteger> counts = mapInt.counts;
        final int n = counts.size();
        int sum = mapInt.sum;
        Double[] probabilities = getProbabilities(counts, n, sum);
        Arrays.sort(costs);

        Tree best = getBest(probabilities, costs);
        return getMap(counts, best);
    }

    private static Map<Character, String> getMap(final Map<Character, AtomicInteger> counts, final Tree tree) {
        final Node root = tree.nodes.get(0);
        if (root.parent != null) {
            throw new IllegalStateException("Root has a parent");
        }
        root.startRecursiveCodeAssign();

        Map<Character, String> result = new HashMap<>();
        Queue<Character> sortedChars = getSortedChars(counts);
        Collections.sort(tree.leaves);
        while (sortedChars.size() > 0) {
            Character key = sortedChars.poll();
            Node node = tree.leaves.get(0);
            tree.leaves.remove(0);
            assert node != null;
            result.put(key, node.code);
        }
        return result;
    }

    private static Queue<Character> getSortedChars(final Map<Character, AtomicInteger> counts) {
        PriorityQueue<CharInt> buff = new PriorityQueue<>(counts.size(), Collections.reverseOrder());
        for (Character key : counts.keySet()) {
            buff.add(new CharInt(key, counts.get(key).get()));
        }
        //TODO remove
        Queue<Character> result = new ArrayDeque<>(buff.size());
        while (buff.size() > 0) {
            result.add(buff.poll().character);
        }
        return result;
    }

    private static Tree getBest(final Double[] probabilities, final int[] costs) {
        Tree tree = new Tree(costs);
        Tree best = null;
        double bestCost = Double.MAX_VALUE;
        final int n = probabilities.length;
        int maxSteps = 0;
        int stepSum = 0;
        int stepCount = 0;
        //TODO check why working estimation
        int maxLeaves = Math.min(n + costs.length * 3, n * (costs.length - 1));
        while (tree.getLeafCount() <= maxLeaves) {
            if (tree.getLeafCount() >= n) {
                Tree optimized = tree.clone();
                int steps = optimized.optimize(probabilities, OPTIMIZATION_STEPS);
                maxSteps = Math.max(maxSteps, steps);
                stepSum += steps;
                stepCount++;
                double cost = optimized.getCost(probabilities);
                if (cost <= bestCost) {
                    bestCost = cost;
                    best = optimized;
                }
            }
            tree.expand();
        }
        System.out.println("n=" + n + " r=" + costs.length);
        System.out.println("Max steps: " + maxSteps + " avg steps: " + (stepSum / (double) stepCount));
        assert best != null;
        System.out.println("Best tree leaf count: " + best.getLeafCount() + "/" + (n * 2) +
                " with cost: " + bestCost);
        return best;
    }

    private static Double[] getProbabilities(final Map<Character, AtomicInteger> counts, final int n, final double sum) {
        Double[] probabilities = new Double[n];
        int i = 0;
        for (Character key : counts.keySet()) {
            probabilities[i] = counts.get(key).get() / sum;
            i++;
        }
        Arrays.sort(probabilities, Collections.reverseOrder());
        return probabilities;
    }

    private static MapInt getCounts(final String msg) {
        Map<Character, AtomicInteger> counts = new HashMap<>();
        int sum = 0;
        final char[] chars = msg.toCharArray();
        for (char curr : chars) {
            counts.computeIfAbsent(curr, k -> new AtomicInteger()).incrementAndGet();
            sum++;
        }
        return new MapInt(counts, sum);
    }

    private record CharInt(Character character, int count) implements Comparable<CharInt> {

        @Override
        public int compareTo(final CharInt o) {
            return Integer.compare(count, o.count);
        }
    }

    private record MapInt(Map<Character, AtomicInteger> counts, int sum) {
    }
}
