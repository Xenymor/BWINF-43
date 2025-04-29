package cc.retzlaff.timon.round2.schmucknachrichten.extension;

import java.util.*;

public class Tree {
    final int[] depths;

    final List<Node> nodes;
    final List<Node> leaves;

    public Tree(final int[] depths) {
        this.depths = depths;
        this.nodes = new ArrayList<>(depths.length);
        this.leaves = new ArrayList<>();
        final Node root = new Node(0, null);
        nodes.add(root);
        leaves.add(root);
    }

    public Tree clone() {
        final Tree clone = new Tree(depths);
        clone.nodes.clear();
        clone.leaves.clear();
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);
            final Node cloneNode = new Node(node.depth, null);
            cloneNode.index = node.index;
            clone.nodes.add(cloneNode);
            if (node.isLeaf) {
                clone.leaves.add(cloneNode);
            }
            cloneNode.code = node.code;
        }
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);
            final Node cloneNode = clone.nodes.get(i);
            cloneNode.parent = node.parent == null ? null : clone.nodes.get(nodes.indexOf(node.parent));
            List<Node> children = node.children;
            for (final Node child : children) {
                final Node cloneChild = clone.nodes.get(nodes.indexOf(child));
                cloneNode.addChild(cloneChild);
            }
        }
        return clone;
    }

    public int getLeafCount() {
        return leaves.size();
    }

    public double getCost(final Double[] probabilities) {
        int i = 0;
        double cost = 0;
        Collections.sort(leaves);
        for (Node node : leaves) {
            if (i >= probabilities.length) {
                break;
            }
            cost += node.depth * probabilities[i];
            i++;
        }
        return cost;
    }

    public void expandHighest() {
        if (leaves.size() > 0) {
            final Node node = leaves.get(0);
            expand(node, 0);
        } else {
            System.out.println("No more leaves to expandHighest.");
        }
    }

    private void expand(final Node node, int index) {
        leaves.remove(index);
        for (final int depth : depths) {
            final Node child = new Node(node.depth + depth, node);
            node.addChild(child);
            nodes.add(child);
            leaves.add(child);
        }
        node.assignDepthsRecursively(depths);
    }

    public int optimize(final Double[] probabilities) {
        int i = 0;
        while (true) {
            if (!optimizationStep(probabilities)) {
                return i + 1;
            }
            i++;
        }
    }

    private boolean optimizationStep(final Double[] probabilities) {
        int bestAction = -1;
        double bestCost = Double.MAX_VALUE;

        bestAction = getBestAction(probabilities, bestAction, bestCost);

        return applyAction(bestAction);
    }

    private boolean applyAction(final int bestAction) {
        if (bestAction == -1) {
            return false;
        } else {
            int nodeIndex = bestAction & 0xFFFF;
            int targetDepth = bestAction >> 16;
            Node node = nodes.get(nodeIndex);
            Set<Node> descendants = node.getDescendants();
            Node target = null;
            for (Node leaf : leaves) {
                if (leaf.depth == targetDepth && !descendants.contains(leaf)) {
                    target = leaf;
                    break;
                }
            }
            if (target == null) {
                return false;
            }
            if (target.equals(node)) {
                return false;
            }
            leaves.remove(target);
            target.addChildren(node.children, depths);
            node.clearChildren();
            leaves.add(node);
            return true;
        }
    }

    private final Set<Integer> testedDepths = new HashSet<>();

    private int getBestAction(final Double[] probabilities, int bestAction, double bestCost) {
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);
            if (node.isLeaf) {
                continue;
            }
            testedDepths.clear();
            List<Node> children = new ArrayList<>(node.children);
            Set<Node> descendants = node.getDescendants();
            node.clearChildren();
            leaves.add(node);
            List<Node> leavesClone = new ArrayList<>(leaves);
            for (final Node leaf : leavesClone) {
                if (descendants.contains(leaf) || testedDepths.contains(leaf.depth)) {
                    continue;
                }
                testedDepths.add(leaf.depth);
                if (leaf.children.size() > 0) {
                    System.out.println("Leaf has children: " + leaf + " " + leaf.children.size() + " " + leaf.depth);
                }
                leaves.remove(leaf);
                leaf.addChildren(children, depths);
                double cost = getCost(probabilities);
                leaf.clearChildren();
                leaves.add(leaf);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestAction = i + (leaf.depth << 16);
                }
            }
            node.addChildren(children, depths);
            leaves.remove(node);
        }
        return bestAction;
    }

    public Node createMarker(int n) {
        nodes.get(0).startRecursiveCodeAssign();
        Collections.sort(leaves);
        Set<String> codes = getCodeSet(n);
        for (int j = n; j < leaves.size(); j++) {
            final Node potentialMarker = leaves.get(j);
            boolean isMarker = isMarker(codes, potentialMarker, n);
            if (isMarker) {
                return potentialMarker;
            }
        }
        int index = n;
        while (true) {
            boolean nLeaves = leaves.size() == n;
            if (nLeaves) {
                index--;
            }
            final Node node = leaves.get(index);
            expand(node, index);
            node.assignCodesRecursively(node.parent.code, node.index);
            Collections.sort(leaves);
            codes = getCodeSet(n);
            final List<Node> newNodes = node.children;
            for (int i = nLeaves ? 1 : 0; i < newNodes.size(); i++) {
                final Node potentialMarker = newNodes.get(i);
                if (isMarker(codes, potentialMarker, n)) {
                    return potentialMarker;
                }
            }
            if (nLeaves) {
                index++;
            }
        }
    }

    private boolean isMarker(final Set<String> codes, final Node potentialMarker, int n) {
        boolean isMarker = true;
        leaves.add(n, potentialMarker);
        for (int j = 0; j < n+1; j++) {
            final Node leaf = leaves.get(j);
            if (leaf.equals(potentialMarker)) {
                continue;
            }
            for (int i = 1; i <= Math.min(leaf.code.length(), potentialMarker.code.length()); i++) {
                if (leaf.code.substring(leaf.code.length() - i).equals(potentialMarker.code.substring(0, i))) {
                    int lastIndex = i;
                    for (int k = i; k < potentialMarker.code.length(); k++) {
                        final String substring = potentialMarker.code.substring(lastIndex, k + 1);
                        if (codes.contains(substring)) {
                            lastIndex = k + 1;
                        }
                    }
                    if (lastIndex < potentialMarker.code.length()) {
                        for (int k = 0; k < n; k++) {
                            final Node oLeaf = leaves.get(k);
                            if (oLeaf.code.startsWith(potentialMarker.code.substring(lastIndex))) {
                                isMarker = false;
                                break;
                            }
                        }
                    } else {
                        isMarker = false;
                        break;
                    }
                }
            }
            if (!isMarker) {
                break;
            }
        }
        leaves.remove(n);
        return isMarker;
    }

    private Set<String> getCodeSet(final int n) {
        Set<String> codeSet = new HashSet<>();
        for (int i = 0; i < n; i++) {
            final Node leaf = leaves.get(i);
            codeSet.add(leaf.code);
        }
        return codeSet;
    }
}
