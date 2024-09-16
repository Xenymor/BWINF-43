package cc.retzlaff.timon.grabmal;

public class Gate {
    final int period;

    public Gate(final int period) {
        this.period = period;
    }

    public boolean isOpen(final int time) {
        return ((time / period) & 1) == 1;
    }
}
