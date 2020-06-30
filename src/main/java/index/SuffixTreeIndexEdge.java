package index;

import java.util.List;

public class SuffixTreeIndexEdge {
    private List<String> text;
    private SuffixTreeIndexNode childNode;

    SuffixTreeIndexEdge() {
        this.text = null;
        this.childNode = null;
    }

    SuffixTreeIndexEdge(List<String> edgeText, SuffixTreeIndexNode childNode) {
        this.text = edgeText;
        this.childNode = childNode;
    }

    SuffixTreeIndexNode getChild() {
        return childNode;
    }

    void setChild(SuffixTreeIndexNode childNode, List<String> text) {
        this.childNode = childNode;
        this.text = text;
    }

    String strAt(int index) {
        return text.get(index);
    }

    int length() {
        return text.size();
    }

    void setText(List<String> text) {
        this.text = text;
    }

    List<String> getText() {
        return this.text;
    }

    boolean isNewLeaf() {
        return (childNode == null);
    }
}

