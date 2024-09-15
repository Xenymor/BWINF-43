package cc.retzlaff.timon.grabmal;

import java.rmi.UnexpectedException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] inputLines = """
                5
                17
                13
                7
                9
                13""".split("\n");
        Gate[] gates = parseGates(inputLines);
        Path path = findPath(gates);
        System.out.println(path);
    }

    private static Path findPath(final Gate[] gates) throws Exception {
        Map<State, State> previous = new HashMap<>();
        List<State> toCheck = new ArrayList<>();
        State state = new State(gates, 0, -1);
        toCheck.add(state);
        do {
            state = toCheck.get(0);
            toCheck.remove(0);
            List<Move> allMoves = getAllMoves(state);
            for (Move move : allMoves) {
                State futureState = getState(state, move);
                if (previous.containsKey(futureState)) {
                    final State state1 = previous.get(futureState);
                    if (state.time() < state1.time()) {
                        previous.put(futureState, state);
                    }
                } else {
                    previous.put(futureState, state);
                    addState(toCheck, futureState);
                }
                if (futureState.position() == gates.length - 1) {
                    return getPath(futureState, previous);
                }
            }
        } while (state.position() < gates.length);
        throw new Exception("Couldn't find a path");
    }

    private static Path getPath(final State targetState, final Map<State, State> previous) throws UnexpectedException {
        List<Move> moves = new ArrayList<>();
        State curr = targetState;
        while (previous.containsKey(curr)) {
            State next = previous.get(curr);
            if (next.position() != curr.position()) {
                moves.add(new Move(Directions.MOVE, curr.position() - next.position()));
            } else if (next.time() != curr.time()) {
                moves.add(new Move(Directions.WAIT, curr.time() - next.time()));
            } else {
                throw new UnexpectedException("???");
            }
            curr = next;
        }
        Collections.reverse(moves);
        return new Path(moves);
    }

    private static void addState(final List<State> toCheck, final State futureState) {
        for (int i = 0; i < toCheck.size(); i++) {
            if (toCheck.get(i).time() > futureState.time()) {
                toCheck.add(i, futureState);
                return;
            }
        }
        toCheck.add(futureState);
    }

    private static State getState(final State state, final Move move) {
        switch (move.direction()) {
            case WAIT -> {
                return new State(state.gates(), state.time() + move.number(), state.position());
            }
            case MOVE -> {
                return new State(state.gates(), state.time(), state.position() + move.number());
            }
            default -> throw new IllegalArgumentException("The move direction " + move.direction() + " was not expected");
        }
    }

    private static List<Move> getAllMoves(final State state) {
        List<Move> result = new ArrayList<>();

        int position = state.position();
        final Gate[] gates = state.gates();
        if (position > -1 && !gates[position].open) {
            return result;
        }

        result.add(new Move(Directions.WAIT, getTimeUntilNextOpen(state, position)));
        if (position != -1) {
            final int timeUntilLastOpen = getTimeUntilLastOpen(state, position);
            if (timeUntilLastOpen != Integer.MAX_VALUE)
                result.add(new Move(Directions.WAIT, timeUntilLastOpen));
        }

        for (int i = position + 1; i < gates.length; i++) {
            if (gates[i].open) {
                result.add(new Move(Directions.MOVE, i - position));
            } else {
                break;
            }
        }
        if (position > 0) {
            for (int i = position - 1; i >= 0; i--) {
                if (gates[i].open) {
                    result.add(new Move(Directions.MOVE, i - position));
                } else {
                    break;
                }
            }
        }
        return result;
    }

    private static int getTimeUntilLastOpen(final State state, final int position) {
        Gate[] gates = state.gates();
        for (int i = position - 1; i >= 0; i--) {
            final Gate gate = gates[i];
            if (!gate.open) {
                return getTimeUntilOpen(gate, state.time());
            }
        }
        return Integer.MAX_VALUE;
    }

    private static int getTimeUntilNextOpen(final State state, final int position) {
        Gate[] gates = state.gates();
        for (int i = position + 1; i < gates.length; i++) {
            final Gate gate = gates[i];
            if (!gate.open) {
                return getTimeUntilOpen(gate, state.time());
            }
        }
        System.out.println("Unnecessary call");
        return Integer.MAX_VALUE;
    }

    private static int getTimeUntilOpen(final Gate gate, final int time) {
        return gate.open ? 0 : gate.period - (time % gate.period);
    }

    private static Gate[] parseGates(final String[] input) {
        Gate[] result = new Gate[Integer.parseInt(input[0])];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Gate(Integer.parseInt(input[i + 1]));
        }
        return result;
    }
}
