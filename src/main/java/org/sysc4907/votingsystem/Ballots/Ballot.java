package org.sysc4907.votingsystem.Ballots;

public class Ballot {
    private final int id; // may want a helper class to generate these. Must be random and unique.
    private final Marks[] markableBoxes; // true if voting for candidate in that row, false otherwise.
    private final int candidateOrder; // plain text to start but will need to be encrypted in the future.

    public Ballot(int id, int numberOfCandidates, int candidateOrder, boolean[] premarkedBoxes) {
        this.id = id;
        markableBoxes = new Marks[numberOfCandidates];
        this.candidateOrder = candidateOrder;
        for (int i = 0; i < premarkedBoxes.length; i++) {
            if (premarkedBoxes[i]) {
                markableBoxes[i] = new Marks(true, true);
            } else {
                markableBoxes[i] = new Marks(false, false);
            }
        }
    }

    /**
     * cast ballot
     * @return true if ballot was cast successfully, false otherwise.
     */
    public boolean castBallot() {
        // interact with blockchain
        return false;
    }

    /**
     * get a list of where the boxes are marked or not.
     * @return array of whether a mark exists or not.
     */
    public boolean[] getMarkValues() {
        boolean[] marks = new boolean[markableBoxes.length];
        for (int i = 0; i < markableBoxes.length; i++) {
            marks[i] = markableBoxes[i].isMarked();
        }
        return marks;
    }

    public void setMarkableBox(int index, Boolean value) {
        markableBoxes[index].setMarked(value);
    }

    public boolean isBoxMarked(int box) {
        return markableBoxes[box].marked;
    }

    public int getCandidateOrder() {
        return candidateOrder;
    }

    public int getId() {
        return id;
    }

    public boolean isPremark(int box) {
        return markableBoxes[box].isPremark();
    }


    private class Marks {
        public boolean marked;
        public final boolean isPremark; // the auto generated marks for the three ballot system. Can't remove these marks.

        public Marks(boolean marked, boolean isPremark) {
            this.marked = marked;
            this.isPremark = isPremark;
        }

        public boolean isMarked() {
            return marked;
        }

        public void setMarked(boolean marked) {
            if (isPremark) return; // don't allow premarks to change
            this.marked = marked;
        }

        public boolean isPremark() {
            return isPremark;
        }
    }
}

