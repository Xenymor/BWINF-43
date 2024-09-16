package cc.retzlaff.timon.grabmal;

import java.util.List;

public record Path(List<State> states) {

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        final String moveString = "Laufe in den Abschnitt";
        final String waitString = "Warte für";
        if (states.size() > 1) {
            boolean waiting = false;
            int count = 0;
            State last = states.get(0);
            for (int i = 1; i < states.size(); i++) {
                final State state = states.get(i);
                if (state.time() != last.time()) {
                    if (!waiting) {
                        if (last.position() != -1) {
                            stringBuilder.append(moveString).append(" ").append(last.position() + 1).append("; ");
                        }
                        waiting = true;
                    }
                    count += state.time() - last.time();
                } else if (state.position() != last.position()) {
                    if (waiting) {
                        stringBuilder.append(waitString).append(" ").append(count).append(" ").append(count == 1 ? "Minute; " : "Minuten; ");
                        waiting = false;
                        count = 0;
                    }
                }
                last = state;
            }
            if (count != 0) {
                stringBuilder.append(waitString).append(" ").append(count).append(" ").append(count == 1 ? "Minute; " : "Minuten; ");
            }
        }
        String result = stringBuilder.toString();
        final String finishString = "Laufe zum Grabmal";
        if (!result.matches("(?s).*" + moveString + " [0123456789]+; $")) {
            result += finishString;
        } else {
            result = result.replaceAll(moveString + " [0123456789]+; $", finishString);
        }
        return result;
    }
}
