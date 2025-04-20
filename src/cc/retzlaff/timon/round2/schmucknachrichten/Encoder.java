package cc.retzlaff.timon.round2.schmucknachrichten;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Encoder {
    public static Map<Character, String> generateTable(final String msg, final int[] costs) {
        final MapInt mapInt = getCounts(msg);
        Map<Character, AtomicInteger> counts = mapInt.counts;
        final int n = counts.size();
        int sum = mapInt.sum;
        Double[] probabilities = getProbabilities(counts, n, sum);

        Arrays.sort(costs);
        final int maxCost = costs[costs.length - 1];
        final int[] d = getD(costs, maxCost);

        Signature best = getBest(costs, n, probabilities, maxCost, d);

        return getTable(best, d, counts);
    }

    private static Map<Character, String> getTable(final Signature bottom, final int[] d, final Map<Character, AtomicInteger> counts) {
        List<Signature> signatures = getSignatures(bottom);

        List<Node>[] tree = initializeTree(signatures);
        buildTree(d, signatures, tree);

        return getMap(counts, tree);
    }

    private static Map<Character, String> getMap(final Map<Character, AtomicInteger> counts, final List<Node>[] tree) {
        tree[0].get(0).startRecursiveCodeAssign();

        Map<Character, String> result = new HashMap<>();
        Queue<Character> sortedChars = getSortedChars(counts);
        int layerInd = 1;
        List<Node> layer = tree[layerInd];
        int index = 0;
        Character curr = sortedChars.poll();
        while (true) {
            final Node node = layer.get(index);
            if (node.isLeaf) {
                result.put(curr, node.getCode());
                if (sortedChars.size() == 0) {
                    break;
                }
                curr = sortedChars.poll();
            }
            index++;
            if (index >= layer.size()) {
                index = 0;
                layerInd++;
                layer = tree[layerInd];
            }
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

    private static void buildTree(final int[] d, final List<Signature> signatures, final List<Node>[] tree) {
        int[] prevSig;
        int[] sig = signatures.get(0).signature;
        for (int i = 0; i < tree.length; i++) {
            prevSig = sig;
            sig = signatures.get(i).signature;
            int leafCount = sig[0] - (i == 0 ? 0 : prevSig[0]);
            final List<Node> layer = tree[i];
            for (int j = leafCount; j < layer.size(); j++) {
                expand(layer.get(j), d, tree);
            }
        }
    }

    private static void expand(final Node node, final int[] d, final List<Node>[] tree) {
        for (int i = 0; i < d.length; i++) {
            final int depth = node.depth + i;
            for (int j = 0; j < d[i]; j++) {
                final Node child = new Node(depth);
                node.addChild(child);
                tree[child.depth].add(child);
            }
        }
    }

    private static List<Node>[] initializeTree(final List<Signature> signatures) {
        List<Node>[] tree = new List[signatures.size()];
        Arrays.setAll(tree, i -> new ArrayList<>());

        final Node rootNode = new Node(0);
        tree[0].add(rootNode);

        return tree;
    }

    private static List<Signature> getSignatures(final Signature bottom) {
        List<Signature> signatures = new ArrayList<>();
        Signature currSignature = bottom;
        while (currSignature != null) {
            signatures.add(currSignature);
            currSignature = currSignature.previous;
        }
        Collections.reverse(signatures);
        return signatures;
    }

    private static Signature getBest(final int[] costs, final int n, final Double[] probabilities, final int maxCost, final int[] d) {
        Signature best = new Signature(new int[]{0}, Double.POSITIVE_INFINITY, null);
        Map<int[], Signature> trees = new HashMap<>();
        PriorityQueue<Signature> toCheck = new PriorityQueue<>();
        final int[] root = new int[maxCost + 1];
        add(root, d, 1);
        root[0] = 0;
        final Signature rootSig = new Signature(root, 0, null);
        toCheck.add(rootSig);
        trees.put(root, rootSig);
        while (toCheck.size() > 0) {
            Signature curr = toCheck.poll();
            int[] sig = curr.signature;
            //TODO check if this line is valid
            trees.remove(sig);
            double newCost = curr.cost + getSum(sig[0], n, probabilities);
            for (int q = 0; q <= sig[1]; q++) {
                int[] newSig = add(shift(sig), d, q);
                if (Arrays.equals(sig, newSig)) {
                    continue;
                }
                final int leafCount = getSum(newSig);
                if (leafCount <= n * (costs.length - 1)) {
                    Signature newSignature;
                    if (trees.containsKey(newSig)) {
                        newSignature = trees.get(newSig);
                        if (newSignature.cost > newCost) {
                            newSignature.changePath(curr, newCost);
                        }
                    } else {
                        newSignature = new Signature(newSig, newCost, curr);
                        trees.put(newSig, newSignature);
                        toCheck.add(newSignature);
                    }
                    if (newSig[0] >= n) {
                        if (newSignature.cost < best.cost) {
                            best = newSignature;
                        }
                    }
                }
            }
        }
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
