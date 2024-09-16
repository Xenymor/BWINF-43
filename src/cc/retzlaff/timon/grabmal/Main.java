package cc.retzlaff.timon.grabmal;

import java.nio.file.Files;
import java.util.*;

import static cc.retzlaff.timon.grabmal.MoveType.MOVE;
import static cc.retzlaff.timon.grabmal.MoveType.WAIT;

public class Main {
    public static void main(String[] args) throws Exception {
        List<String> inputLines = Files.readAllLines(java.nio.file.Path.of(args[0]));
        Gate[] gates = parseGates(inputLines);
        long start = System.nanoTime();
        Path path = findPath(gates);
        long finish = System.nanoTime();
        System.out.println(path);
        System.out.println((finish - start) / 1_000_000.0);
    }

    private static Path findPath(final Gate[] gates) throws Exception {
        Map<State, State> previous = new HashMap<>();
        List<State> toCheck = new ArrayList<>();
        State state = new State(gates, 0, -1);
        toCheck.add(state);
        do {
            state = toCheck.get(0);
            toCheck.remove(0);
            if (state.time() == 27 && state.position() == 3) {
                System.out.println();
            }
            List<Move> allMoves = getAllMoves(state);
            for (Move move : allMoves) {
                State futureState = getState(state, move);
                if (move.direction().equals(WAIT) && futureState.position() >= 0) {
                    if (move.number() >= getTimeUntil(state.gates()[state.position()], false, state.time())) {
                        continue;
                    }
                }
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

    private static int getTimeUntil(final Gate gate, final boolean isOpen, final int time) {
        return gate.isOpen(time) == isOpen ? 0 : gate.period - (time % gate.period);
    }

    private static Path getPath(final State targetState, final Map<State, State> previous) {
        List<State> states = new ArrayList<>();
        State curr = targetState;
        while (previous.containsKey(curr)) {
            State next = previous.get(curr);
            states.add(next);
            curr = next;
        }
        Collections.reverse(states);
        return new Path(states);
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
        final int stateTime = state.time();

        int position = state.position();
        final Gate[] gates = state.gates();
        if (position > -1 && !gates[position].isOpen(stateTime)) {
            return result;
        }
        if (position == -1) {
            final Gate gate = gates[0];
            if (!gate.isOpen(stateTime)) {
                result.add(new Move(WAIT, getTimeUntil(gate, true, stateTime)));
                return result;
            } else {
                final int dt1 = getTimeUntil(gate, false, stateTime);
                final int time = stateTime + dt1;
                result.add(new Move(WAIT, getTimeUntil(gate, true, time) + dt1));
            }
        } else {
            getWaitingMovesFor(gates[position], gates[position + 1], stateTime, result);
            if (position > 0) {
                getWaitingMovesFor(gates[position], gates[position - 1], stateTime, result);
            }
        }

        if (gates[position + 1].isOpen(stateTime)) {
            result.add(new Move(MOVE, 1));
        }
        if (position > 0) {
            if (gates[position - 1].isOpen(stateTime)) {
                result.add(new Move(MOVE, -1));
            }
        }
        return result;
    }

    private static void getWaitingMovesFor(final Gate currGate, final Gate neighbouringGate, final int time, final List<Move> result) {
        int currTime = time;
        int timeSpan = getTimeUntil(currGate, false, currTime);
        while (timeSpan > 0) {
            final int timeUntilNextGateOpen = getTimeUntil(neighbouringGate, true, currTime);
            if (timeSpan > timeUntilNextGateOpen) {
                timeSpan -= timeUntilNextGateOpen;
                currTime += timeUntilNextGateOpen;
                result.add(new Move(WAIT, currTime - time));

                final int timeUntilNextGateClosed = getTimeUntil(neighbouringGate, false, currTime);
                timeSpan -= timeUntilNextGateClosed;
                currTime += timeUntilNextGateClosed;
            } else {
                break;
            }
        }
    }

    private static Gate[] parseGates(final List<String> input) {
        Gate[] result = new Gate[Integer.parseInt(input.get(0))];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Gate(Integer.parseInt(input.get(i + 1)));
        }
        return result;
    }
}
