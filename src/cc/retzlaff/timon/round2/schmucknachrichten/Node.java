package cc.retzlaff.timon.round2.schmucknachrichten;

import java.util.ArrayList;
import java.util.List;

public class Node {
    boolean isLeaf;
    final List<Node> children;
    String code;
    int depth;

    public Node(final int depth) {
        this.children = new ArrayList<>();
        this.depth = depth;
        isLeaf = true;
    }

    public void addChild(Node node) {
        children.add(node);
        isLeaf = false;
    }

    public String getCode() {
        return code;
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
}
