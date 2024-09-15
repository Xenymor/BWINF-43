package cc.retzlaff.timon.grabmal;

public class Gate {
    final int period;
    boolean open;

    public Gate(final int period) {
        this.period = period;
        open = false;
    }

    public Gate(final int period, final boolean open) {
        this.period = period;
        this.open = open;
    }
}
