package index;

import java.util.HashMap;

class SuffixTreeOverlayNode {
    private final HashMap<String, SuffixTreeOverlayEdge> childNodes;
    private SuffixTreeOverlayNode suffixLink;
    private final SuffixTreeIndexNode stIndexNode;

    SuffixTreeOverlayNode(SuffixTreeIndexNode stIndexNode) {
        childNodes = new HashMap<>();
        suffixLink = null;
        this.stIndexNode = stIndexNode;
    }

    /**
     * add a new edge (the edge has startIndex, endIndex and next node)
     * adding an initialized edge is equivalent to adding the transition g'(s, (k,x)) = r
     */
    void addChild(String childStr, SuffixTreeOverlayEdge edge) {
        childNodes.put(childStr, edge);
    }

    SuffixTreeOverlayEdge getChild(String childStr) {
        return childNodes.get(childStr);
    }

    void setSuffixLink(SuffixTreeOverlayNode suffixLink) {
        this.suffixLink = suffixLink;
    }

    SuffixTreeOverlayNode getSuffixLink() {
        return suffixLink;
    }

    SuffixTreeIndexNode getStIndexNode() {
        return stIndexNode;
    }

    boolean isLeaf() {
        return childNodes.isEmpty();
    }
}

