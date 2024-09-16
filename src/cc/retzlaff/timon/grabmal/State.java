package cc.retzlaff.timon.grabmal;

import java.util.Objects;

public record State(Gate[] gates, int time, int position) {

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
