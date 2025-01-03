package cc.retzlaff.timon.round2.simultaneLabyrinthe;

public class Field {
    boolean hasRightWall = true;
    boolean hasLowerWall = true;
    boolean isHole = false;

    void setRightWall(final boolean newValue) {
        hasRightWall = newValue;
    }

    void setLowerWall(final boolean newValue) {
        hasLowerWall = newValue;
    }

    void setIsHole(final boolean newValue) {
        isHole = newValue;
    }
}
