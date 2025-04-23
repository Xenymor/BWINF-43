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
            System.out.println("No more leaves to expand.");
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

    private int getBestAction(final Double[] probabilities, int bestAction, double bestCost) {
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);
            if (node.isLeaf) {
                continue;
            }
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

    public Tree deepOptimize(final Double[] probabilities, final int optimizationSteps, final int candidateCount) {
        Tree[] candidates = new Tree[candidateCount];
        ActionCost[] bestActions = getBestActions(probabilities, candidateCount);
        for (int i = 0; i < candidateCount; i++) {
            candidates[i] = clone();
            candidates[i].applyAction(bestActions[i].action());
        }

        ActionCost[] actions = new ActionCost[candidateCount];
        ActionCost filler = new ActionCost(-1, Double.MAX_VALUE, null);
        for (int i = 0; i < optimizationSteps; i++) {
            Arrays.fill(actions, filler);
            for (int j = 0; j < candidateCount; j++) {
                ActionCost[] currActions = candidates[j].getBestActions(probabilities, candidateCount);
                for (int k = 0; k < candidateCount; k++) {
                    insert(actions, currActions[k]);
                }
            }
            for (int j = 0; j < candidateCount; j++) {
                final ActionCost action = actions[j];
                final Tree clone = action.tree().clone();
                candidates[j] = clone;
                clone.applyAction(action.action());
            }
        }
        double bestCost = Double.MAX_VALUE;
        int bestIndex = -1;
        for (int i = 0; i < candidateCount; i++) {
            double cost = candidates[i].getCost(probabilities);
            if (cost < bestCost) {
                bestCost = cost;
                bestIndex = i;
            }
        }
        if (bestIndex != 0) {
            System.out.println("Best tree not first candidate");
        }
        return candidates[bestIndex];
    }

    private ActionCost[] getBestActions(final Double[] probabilities, final int count) {
        ActionCost[] result = new ActionCost[count];
        Arrays.fill(result, new ActionCost(-1, Double.MAX_VALUE, null));
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);
            if (node.isLeaf) {
                continue;
            }
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
                if (cost < result[count - 1].cost()) {
                    int action = i + (k << 16);
                    insert(result, new ActionCost(action, cost, this));
                }
            }
            node.addChildren(children, depths);
            leaves.remove(node);
        }
        return result;
    }

    private void insert(final ActionCost[] result, final ActionCost actionCost) {
        for (int i = 0; i < result.length; i++) {
            if (result[i].cost() > actionCost.cost()) {
                if (result.length - 1 - i >= 0) {
                    System.arraycopy(result, i, result, i + 1, result.length - 1 - i);
                }
                result[i] = actionCost;
                return;
            }
        }
    }
}
