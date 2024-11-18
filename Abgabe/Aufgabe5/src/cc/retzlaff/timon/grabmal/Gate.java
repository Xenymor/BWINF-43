package cc.retzlaff.timon.grabmal;

public record Gate(int period) {

    public boolean isOpen(final int time) {
        return ((time / period) & 1) == 1;
    }

    public int getPeriod() {
        return period;
    }

}
