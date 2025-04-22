package cc.retzlaff.timon.round2.schmucknachrichten;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Encoder {
    private static final int OPTIMIZATION_STEPS = 20;

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
        while (tree.getLeafCount() <= n * (costs.length - 1)) {
            if (tree.getLeafCount() >= n) {
                Tree optimized = tree.clone();
                int steps = optimized.optimize(probabilities, OPTIMIZATION_STEPS);
                maxSteps = Math.max(maxSteps, steps);
                stepSum += steps;
                stepCount++;
                double cost = optimized.getCost(probabilities);
                if (cost < bestCost) {
                    bestCost = cost;
                    best = optimized;
                }
            }
            tree.expand();
        }
        System.out.println("n=" + n + " r=" + costs.length);
        System.out.println("Max steps: " + maxSteps + " avg steps: " + (stepSum / (double) stepCount));
        System.out.println("Best tree leaf count: " + best.getLeafCount() + "/" + (n * (costs.length - 1)) +
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

    private static int[] getD(final int[] costs, final int maxCost) {
        final int[] d = new int[maxCost + 1];
        d[0] = -1;
        for (int cost : costs) {
            d[cost]++;
        }
        return d;
    }

    private static int getSum(final int[] arr) {
        int res = 0;
        for (int curr : arr) {
            res += curr;
        }
        return res;
    }

    private static int[] shift(final int[] sig) {
        int[] res = new int[sig.length];
        res[0] = sig[0] + sig[1];
        System.arraycopy(sig, 2, res, 1, sig.length - 2);
        res[res.length - 1] = 0;
        return res;
    }

    private static double getSum(final int start, final int end, final Double[] arr) {
        double sum = 0;
        for (int t = start; t < end; t++) {
            sum += arr[t];
        }
        return sum;
    }

    private static int[] add(final int[] arr, final int[] toAdd, final int factor) {
        for (int i = 0; i < toAdd.length; i++) {
            arr[i] += toAdd[i] * factor;
        }
        return arr;
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

    private static int max(final int[] costs) {
        int max = 0;
        for (int cost : costs) {
            max = Math.max(max, cost);
        }
        return max;
    }

    private static class CharInt implements Comparable<CharInt> {
        public Character character;
        public int count;

        public CharInt(final Character character, final int count) {
            this.character = character;
            this.count = count;
        }

        @Override
        public int compareTo(final CharInt o) {
            return Integer.compare(count, o.count);
        }
    }

    private static class MapInt {
        public Map<Character, AtomicInteger> counts;
        public int sum;

        public MapInt(final Map<Character, AtomicInteger> counts, final int sum) {
            this.counts = counts;
            this.sum = sum;
        }
    }
}
