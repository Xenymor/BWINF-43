package cc.retzlaff.timon.round2.schmucknachrichten;

import java.util.*;

public class Tree {
    final int[] depths;

    List<Node> nodes;
    //TODO optimize
    List<Node> leaves;

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
        final Node node = leaves.get(0);
        leaves.remove(0);
        if (node != null) {
            for (final int depth : depths) {
                final Node child = new Node(node.depth + depth, node);
                node.addChild(child);
                nodes.add(child);
                leaves.add(child);
            }
        } else {
            System.out.println("No more leaves to expand.");
        }
    }

    public void optimize(final Double[] probabilities, final int optimizationSteps) {
        for (int i = 0; i < optimizationSteps; i++) {
            if (!optimizationStep(probabilities)) {
                break;
            }
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
            int targetIndex = bestAction >> 16;
            if (targetIndex >= leaves.size()) {
                return false;
            }
            Node node = nodes.get(nodeIndex);
            Node target = leaves.get(targetIndex);
            assert target != null;
            leaves.remove(target);
            target.addChildren(node.children, depths);
            node.clearChildren();
            leaves.add(node);
            return true;
        }
    }

    int counter = 0;
    private int getBestAction(final Double[] probabilities, int bestAction, double bestCost) {
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);
            if (node.isLeaf) {
                continue;
            }
            counter++;
            if (counter == 2) {
                System.out.println();
            }
            System.out.println(counter);
            List<Node> children = new ArrayList<>(node.children);
            //TODO inefficient
            Set<Node> descendants = node.getDescendants();
            node.clearChildren();
            leaves.add(node);
            List<Node> leavesClone = new ArrayList<>(leaves);
            for (int k = 0; k < leavesClone.size(); k++) {
                final Node leaf = leavesClone.get(k);
                if (descendants.contains(leaf)) {
                    continue;
                }
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
                    bestAction = i + (k << 16);
                }
            }
            node.addChildren(children, depths);
            leaves.remove(node);
        }
        return bestAction;
    }
}
