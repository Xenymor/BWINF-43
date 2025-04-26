package cc.retzlaff.timon.round2.schmucknachrichten.extension;

import java.util.*;

public class Node implements Comparable<Node> {
    boolean isLeaf;
    final List<Node> children;
    String code;
    int depth;
    Node parent;
    int index;

    public Node(final int depth, final Node parent) {
        this.children = new ArrayList<>();
        this.depth = depth;
        isLeaf = true;
        this.parent = parent;
    }

    public void addChild(Node node) {
        node.index = children.size();
        children.add(node);
        node.parent = this;
        isLeaf = false;
    }

    public void startRecursiveCodeAssign() {
        code = "";
        for (int i = 0; i < children.size(); i++) {
            children.get(i).assignCodesRecursively(code, i);
        }
    }

    public void assignCodesRecursively(final String parentCode, final int index) {
        code = parentCode + index;
        if (!isLeaf) {
            for (int i = 0; i < children.size(); i++) {
                children.get(i).assignCodesRecursively(code, i);
            }
        }
    }


    @Override
    public int compareTo(final Node o) {
        final int res = Integer.compare(depth, o.depth);
        return res == 0 ? Integer.compare(System.identityHashCode(this), System.identityHashCode(o)) : res;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Node node = (Node) o;
        return isLeaf == node.isLeaf && depth == node.depth && index == node.index && Objects.equals(children, node.children) && Objects.equals(code, node.code) && parent == node.parent;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    public void clearChildren() {
        children.clear();
        isLeaf = true;
    }

    public void addChildren(final List<Node> children, final int[] depths) {
        if (children.size() != depths.length) {
            throw new IllegalArgumentException("Children and depths must be of the same size");
        }
        for (int i = 0; i < children.size(); i++) {
            final Node child = children.get(i);
            child.depth = depths[i] + depth;
            child.assignDepthsRecursively(depths);
            addChild(child);
        }
    }

    void assignDepthsRecursively(final int[] depths) {
        for (int i = 0; i < children.size(); i++) {
            final Node child = children.get(i);
            child.depth = depths[i] + depth;
            child.assignDepthsRecursively(depths);
        }
    }

    public Set<Node> getDescendants() {
        Set<Node> descendants = new HashSet<>();
        getDescendantsRecursively(descendants);
        return descendants;
    }

    private void getDescendantsRecursively(final Set<Node> descendants) {
        for (Node child : children) {
            descendants.add(child);
            child.getDescendantsRecursively(descendants);
        }
    }

}
