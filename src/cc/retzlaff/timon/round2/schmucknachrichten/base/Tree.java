package cc.retzlaff.timon.round2.schmucknachrichten.base;

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

    public void expand() {
        if (leaves.size() > 0) {
            final Node node = leaves.get(0);
            leaves.remove(0);
            for (final int depth : depths) {
                final Node child = new Node(node.depth + depth, node);
                node.addChild(child);
                nodes.add(child);
                leaves.add(child);
            }
        } else {
            System.out.println("No more leaves to expandHighest.");
        }
    }

    public int optimize(final Double[] probabilities, final int optimizationSteps) {
        for (int i = 0; i < optimizationSteps; i++) {
            if (!optimizationStep(probabilities)) {
                return i + 1;
            }
        }
        return optimizationSteps;
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

}
