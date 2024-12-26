package cc.retzlaff.timon.round2.schmucknachrichten;

import java.util.List;

public class Node {
    final Character character;
    final int value;
    final int colorCount;
    final boolean isLeaf;
    final List<Node> children;
    String code;

    public Node(final Character character, final Integer value, final int colorCount, final boolean isLeaf) {
        this.character = character;
        this.value = value;
        this.colorCount = colorCount;
        this.isLeaf = isLeaf;
        this.children = null;
    }

    public Node(final List<Node> children, final int value, final int colorCount, final boolean isLeaf) {
        this.character = null;
        this.value = value;
        this.colorCount = colorCount;
        this.isLeaf = isLeaf;
        this.children = children;
    }

    public Character getChar() {
        return character;
    }

    public String getCode() {
        return code;
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
