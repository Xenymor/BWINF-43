package cc.retzlaff.timon.round2.simultaneLabyrinthe;

public class Field {
    boolean hasRightWall = true;
    boolean hasLowerWall = true;
    boolean isHole = false;

    public void setRightWall(final boolean newValue) {
        hasRightWall = newValue;
    }

    public void setLowerWall(final boolean newValue) {
        hasLowerWall = newValue;
    }

    public void setIsHole(final boolean newValue) {
        isHole = newValue;
    }

    public boolean hasRightWall() {
        return hasRightWall;
    }

    public boolean hasLowerWall() {
        return hasLowerWall;
    }

    public boolean isHole() {
        return isHole;
    }
}
