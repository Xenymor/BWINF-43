package cc.retzlaff.timon.grabmal;

import java.util.Objects;

public record State(Gate[] gates, int time, int position) {
    public State(final Gate[] gates, final int time, final int position) {
        this.time = time;
        this.position = position;
        this.gates = new Gate[gates.length];
        for (int i = 0; i < gates.length; i++) {
            final Gate gate = gates[i];
            final boolean open = (time / gate.period & 1) == 1;
            this.gates[i] = new Gate(gate.period, open);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final State state1 = (State) o;
        return time == state1.time && position == state1.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, position);
    }
}
