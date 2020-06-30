package index;

import java.util.*;

public class SuffixTreeIndex {
    private final SuffixTreeIndexNode root;

    public SuffixTreeIndex() {
        root = new SuffixTreeIndexNode();
    }

    /**
     * add a text to suffix tree
     */
    public void addText(List<String> text, String matchIndex) {
        new SuffixTreeOverlay(text, matchIndex);
    }

    /**
     * search a text in suffix tree
     */
    public ArrayList<String> search(List<String> searchText) {
        SuffixTreeIndexNode searchNode = root;
        int edgeIndex = 0;
        int length = searchText.size();
        int searchIndex = 0;
        SuffixTreeIndexEdge edge = null;
        boolean match = true;
        ArrayList<String> returnList = new ArrayList<>();

        while (searchIndex < length) {
            if (edge == null) {
                edge = searchNode.getChild(searchText.get(searchIndex));
                if (edge == null) {
                    match = false;
                    break;
                }
                edgeIndex = 0;
            } else {
                if (!searchText.get(searchIndex).equals(edge.strAt(edgeIndex))) {
                    match = false;
                    break;
                }
            }

            if (edgeIndex == (edge.length() - 1)) {
                searchNode = edge.getChild();
                edge = null;
            }

            searchIndex++;
            edgeIndex++;
        }

        if (match) {
            if (edge != null) {
                searchNode = edge.getChild();
            }
            searchNode.fillMatchIds(returnList, Integer.MAX_VALUE);
        }
        return returnList;
    }

    private class SuffixTreeOverlay {
        private final SuffixTreeOverlayNode root;
        private final List<String> text;
        private final String matchIndex;
        private final LinkedList<SuffixTreeOverlayEdge> newLeafEdges;

        SuffixTreeOverlay(List<String> text, String matchIndex) {
            root = new SuffixTreeOverlayNode(SuffixTreeIndex.this.root);
            this.text = text;
            this.matchIndex = matchIndex;
            newLeafEdges = new LinkedList<>();
            build();
        }

        public void build() {
            SKTuple sk = new SKTuple(root, 1);
            int length = text.size();
            for (int i = 1; i <= length; i++) {
                update(sk, i);
                canonize(sk, i);
            }
            addMatchIds(sk, length);

            ListIterator<SuffixTreeOverlayEdge> leafIterator = newLeafEdges.listIterator(0);
            while (leafIterator.hasNext()) {
                SuffixTreeOverlayEdge leafEdge = leafIterator.next();
                leafEdge.getStIndexEdge().setChild(new SuffixTreeIndexNode(matchIndex),
                        text.subList(leafEdge.getStartIndex() - 1, length));
            }
        }

        private class SKTuple {
            int kIndex;
            SuffixTreeOverlayNode sState;

            SKTuple(SuffixTreeOverlayNode sState, int kIndex) {
                this.kIndex = kIndex;
                this.sState = sState;
            }
        }

        private class TestAndSplitReturn {
            boolean endPointReached;
            SuffixTreeOverlayNode rState;

            TestAndSplitReturn(boolean endPointReached, SuffixTreeOverlayNode rState) {
                this.endPointReached = endPointReached;
                this.rState = rState;
            }
        }

        private void update(SKTuple sk, int i) {
            SuffixTreeOverlayNode oldr = root;
            TestAndSplitReturn testAndSplitReturn;
            testAndSplitReturn = testAndSplit(sk, i - 1, text.get(i - 1));

            while (!testAndSplitReturn.endPointReached) {
                SuffixTreeIndexEdge newIndexEdge = new SuffixTreeIndexEdge();
                SuffixTreeOverlayEdge newOverlayEdge = new SuffixTreeOverlayEdge(i, newIndexEdge);

                newLeafEdges.add(newOverlayEdge);
                testAndSplitReturn.rState.getStIndexNode().addChild(text.get(i - 1), newIndexEdge);
                testAndSplitReturn.rState.addChild(text.get(i - 1), newOverlayEdge);

                if (oldr != root) {
                    oldr.setSuffixLink(testAndSplitReturn.rState);
                }
                oldr = testAndSplitReturn.rState;

                if (sk.sState == root) {
                    if (sk.kIndex <= i - 1) {
                        sk.kIndex++;
                    } else {
                        sk.kIndex++;
                        break;
                    }
                } else {
                    sk.sState = sk.sState.getSuffixLink();
                    canonize(sk, i - 1);
                }
                testAndSplitReturn = testAndSplit(sk, i - 1, text.get(i - 1));
            }

            if (oldr != root) {
                oldr.setSuffixLink(sk.sState);
            }
        }

        private TestAndSplitReturn testAndSplit(SKTuple sk, int pIndex, String tVal) {
            SuffixTreeOverlayEdge tOverlayEdge = sk.sState.getChild(text.get(sk.kIndex - 1));
            SuffixTreeIndexEdge tIndexEdge = sk.sState.getStIndexNode().getChild(text.get(sk.kIndex - 1));

            if (sk.kIndex <= pIndex) {
                String nextStr = (tOverlayEdge.isDiscoveryLeaf()) ?
                        tIndexEdge.strAt(pIndex - sk.kIndex + 1) :
                        text.get(tOverlayEdge.getStartIndex() + pIndex - sk.kIndex);

                if (tVal.equals(nextStr)) {
                    return new TestAndSplitReturn(true, sk.sState);
                } else {
                    SuffixTreeIndexNode rIndexState = new SuffixTreeIndexNode();
                    SuffixTreeOverlayNode rOverlayState = new SuffixTreeOverlayNode(rIndexState);

                    SuffixTreeIndexEdge newIndexEdge = new SuffixTreeIndexEdge(text.subList(sk.kIndex - 1, pIndex), rIndexState);
                    if (!tOverlayEdge.isDiscoveryLeaf()) {
                        SuffixTreeOverlayEdge newOverlayEdge = new SuffixTreeOverlayEdge(tOverlayEdge.getStartIndex(),
                                tOverlayEdge.getStartIndex() + pIndex - sk.kIndex, rOverlayState, newIndexEdge);
                        sk.sState.addChild(text.get(sk.kIndex - 1), newOverlayEdge);
                        tOverlayEdge.setStartIndex(tOverlayEdge.getStartIndex() + pIndex - sk.kIndex + 1);
                        rOverlayState.addChild(nextStr, tOverlayEdge);
                    } else {
                        tOverlayEdge.setChild(rOverlayState, tOverlayEdge.getStartIndex() + pIndex - sk.kIndex);
                    }
                    sk.sState.getStIndexNode().addChild(text.get(sk.kIndex - 1), newIndexEdge);
                    if (!tOverlayEdge.isNewLeaf()) {
                        tIndexEdge.setText(tIndexEdge.getText().subList(pIndex - sk.kIndex + 1, text.size()));
                    }

                    rIndexState.addChild(nextStr, tIndexEdge);
                    return new TestAndSplitReturn(false, rOverlayState);
                }
            } else {
                if (tOverlayEdge != null) {
                    return new TestAndSplitReturn(true, sk.sState);
                } else if (tIndexEdge != null) {
                    SuffixTreeOverlayEdge newOverlayEdge = new SuffixTreeOverlayEdge(sk.kIndex, tIndexEdge);
                    sk.sState.addChild(text.get(sk.kIndex - 1), newOverlayEdge);
                    return new TestAndSplitReturn(true, sk.sState);
                } else {
                    return new TestAndSplitReturn(false, sk.sState);
                }
            }
        }

        private void canonize(SKTuple sk, int pIndex) {
            if (pIndex >= sk.kIndex) {
                SuffixTreeOverlayEdge overlayEdge = sk.sState.getChild(text.get(sk.kIndex - 1));
                SuffixTreeIndexEdge indexEdge = sk.sState.getStIndexNode().getChild(text.get(sk.kIndex - 1));

                while (!indexEdge.isNewLeaf()) {
                    if (overlayEdge == null) {
                        overlayEdge = new SuffixTreeOverlayEdge(sk.kIndex, indexEdge);
                    }

                    if (indexEdge.length() - 1 <= (pIndex - sk.kIndex)) {
                        if (overlayEdge.isDiscoveryLeaf()) {
                            SuffixTreeOverlayNode newState = new SuffixTreeOverlayNode(indexEdge.getChild());
                            overlayEdge.setChild(newState, sk.kIndex + indexEdge.length() - 1);

                            SKTuple localSk = new SKTuple(sk.sState, sk.kIndex);
                            if (localSk.sState == root) {
                                localSk.kIndex++;
                            } else {
                                localSk.sState = localSk.sState.getSuffixLink();
                            }
                            canonize(localSk, overlayEdge.getEndIndex());
                            newState.setSuffixLink(localSk.sState);
                        }
                        sk.kIndex = sk.kIndex + indexEdge.length();
                        sk.sState = overlayEdge.getChild();
                        if (pIndex < sk.kIndex) {
                            break;
                        }
                        overlayEdge = sk.sState.getChild(text.get(sk.kIndex - 1));
                        indexEdge = sk.sState.getStIndexNode().getChild(text.get(sk.kIndex - 1));
                    } else {
                        break;
                    }
                }
            }
        }

        private void addMatchIds(SKTuple sk, int pIndex) {
            if (pIndex < sk.kIndex) {
                if (sk.sState.isLeaf()) {
                    sk.sState.getStIndexNode().addMatchIndex(matchIndex);
                }
                if (sk.sState != root) {
                    sk.sState = sk.sState.getSuffixLink();
                    addMatchIds(sk, pIndex);
                }

            } else {
                SuffixTreeOverlayEdge overlayEdge = sk.sState.getChild(text.get(sk.kIndex - 1));
                SuffixTreeIndexEdge indexEdge = sk.sState.getStIndexNode().getChild(text.get(sk.kIndex - 1));
                if (overlayEdge.isDiscoveryLeaf()) {
                    SuffixTreeIndexNode newIndexState = new SuffixTreeIndexNode();
                    SuffixTreeIndexEdge newIndexEdge = new SuffixTreeIndexEdge(text.subList(sk.kIndex - 1, pIndex), newIndexState);
                    sk.sState.getStIndexNode().addChild(text.get(sk.kIndex - 1), newIndexEdge);
                    indexEdge.setText(indexEdge.getText().subList(pIndex - sk.kIndex + 1, text.size()));
                    newIndexState.addChild(indexEdge.getText().get(0), indexEdge);
                    newIndexState.addMatchIndex(matchIndex);
                    if (sk.sState == root) {
                        sk.kIndex++;
                    } else {
                        sk.sState = sk.sState.getSuffixLink();
                    }
                    canonize(sk, pIndex);
                    addMatchIds(sk, pIndex);
                }
            }
        }
    }
}
