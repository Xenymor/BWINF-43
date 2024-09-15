package cc.retzlaff.timon.grabmal;

import java.util.List;

public record Path(List<Move> moves) {

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Move move : moves) {
            result.append(getString(move));
        }
        result.append("Gehe zum Grabmal");
        return result.toString();
    }

    private String getString(final Move move) {
        final int number = move.number();
        switch (move.direction()) {
            case WAIT -> {
                return "Warte für " + number + " " + (number == 1 ? "Minute" : "Minuten") + "; ";
            }
            case MOVE -> {
                return "Gehe " + Math.abs(number) + " " + (number == 1 ? "Block" : "Blöcke") + " nach " + (number > 0 ? "rechts" : "links") + "; ";
            }
            default -> throw new IllegalArgumentException("Move direction " + move.direction() + " was not expected");
        }
    }
}
