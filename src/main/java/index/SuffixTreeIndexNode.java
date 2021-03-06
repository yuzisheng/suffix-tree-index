package index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

class SuffixTreeIndexNode {
    private final HashMap<String, SuffixTreeIndexEdge> childNodes = new HashMap<>();
    private final HashSet<String> matchIndices = new HashSet<>();

    SuffixTreeIndexNode() {
    }

    SuffixTreeIndexNode(String matchIndex) {
        addMatchIndex(matchIndex);
    }

    void addChild(String childStr, SuffixTreeIndexEdge edge) {
        childNodes.put(childStr, edge);
    }

    SuffixTreeIndexEdge getChild(String childStr) {
        return childNodes.get(childStr);
    }

    void addMatchIndex(String matchIndex) {
        matchIndices.add(matchIndex);
    }

    Iterator<String> getMatchIndexIterator() {
        return matchIndices.iterator();
    }

    int fillMatchIds(ArrayList<String> returnList, int maxEntries) {
        int numAdded = 0;
        Iterator<String> matchIdIterator = matchIndices.iterator();
        while (numAdded < maxEntries) {
            if (matchIdIterator.hasNext()) {
                returnList.add(matchIdIterator.next());
                numAdded++;
            } else {
                break;
            }
        }

        if (numAdded == maxEntries) {
            return numAdded;
        }

        Iterator<SuffixTreeIndexEdge> childNodeIterator = childNodes.values().iterator();

        while (numAdded < maxEntries) {
            if (childNodeIterator.hasNext()) {
                numAdded += childNodeIterator.next().getChild().fillMatchIds(returnList, maxEntries - numAdded);
            } else {
                break;
            }
        }
        return numAdded;
    }
}

