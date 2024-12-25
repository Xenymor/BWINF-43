package cc.retzlaff.timon.round1.grabmal;

import java.nio.file.Files;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        List<String> inputLines = Files.readAllLines(java.nio.file.Path.of(args[0]));
        Gate[] gates = parseGates(inputLines);
        long start = System.nanoTime();
        List<State> states = findPath(gates);
        long end = System.nanoTime();
        System.out.println(pathToString(states));
        System.out.println("Found in " + (end-start)/1_000_000f + "ms");
    }

    private static String pathToString(final List<State> states) {
        StringBuilder stringBuilder = new StringBuilder();
        final String moveString = "Laufe in den Abschnitt ";
        final String waitString = "Warte für ";
        if (states.size() > 1) {
            boolean waiting = false;
            int count = 0;
            State last = states.get(0);
            for (int i = 1; i < states.size(); i++) {
                final State state = states.get(i);
                if (state.time() != last.time()) {
                    if (!waiting) {
                        if (last.position() != -1) {
                            stringBuilder.append(moveString).append(last.position() + 1).append("; ");
                        }
                        waiting = true;
                    }
                    count += state.time() - last.time();
                } else if (state.position() != last.position()) {
                    if (waiting) {
                        stringBuilder.append(waitString).append(count).append(" ").append(count == 1 ? "Minute; " : "Minuten; ");
                        waiting = false;
                        count = 0;
                    }
                }
                last = state;
            }
            if (count != 0) {
                stringBuilder.append(waitString).append(count).append(" ").append(count == 1 ? "Minute; " : "Minuten; ");
            }
        }
        String result = stringBuilder.toString();
        final String finishString = "Laufe zum Grabmal";
        if (!result.matches("(?s).*" + moveString + "[0123456789]+; $")) {
            result += finishString;
        } else {
            result = result.replaceAll(moveString + "[0123456789]+; $", finishString);
        }
        return result;
    }

    private static List<State> findPath(final Gate[] gates) throws Exception {
        Map<State, State> previous = new HashMap<>();
        List<State> toCheck = new ArrayList<>();
        State state = new State(0, -1);
        toCheck.add(state);
        do {
            state = toCheck.get(0);
            toCheck.remove(0);
            List<State> allChildStates = getAllChildStates(state, gates);
            for (State childState : allChildStates) {
                final int stateTime = state.time();
                final boolean isWaitingMove = stateTime != childState.time();
                if (isWaitingMove && childState.position() >= 0) {
                    if ((childState.time() - stateTime) >= getTimeUntil(gates[state.position()], false, stateTime)) {
                        // I died here!
                        continue;
                    }
                }
                if (previous.containsKey(childState)) {
                    final State state1 = previous.get(childState);
                    if (stateTime < state1.time()) {
                        previous.put(childState, state);
                    }
                } else {
                    previous.put(childState, state);
                    addState(toCheck, childState);
                }
                if (childState.position() == gates.length - 1) {
                    return getPath(childState, previous);
                }
            }
        } while (state.position() < gates.length);
        throw new Exception("Couldn't find a path");
    }

    private static int getTimeUntil(final Gate gate, final boolean isOpen, final int time) {
        return gate.isOpen(time) == isOpen ? 0 : gate.getPeriod() - (time % gate.getPeriod());
    }

    private static List<State> getPath(final State targetState, final Map<State, State> currToPredecessor) {
        List<State> states = new ArrayList<>();
        State curr = targetState;
        while (currToPredecessor.containsKey(curr)) {
            State previous = currToPredecessor.get(curr);
            states.add(previous);
            curr = previous;
        }
        Collections.reverse(states);
        return states;
    }

    private static void addState(final List<State> toCheck, final State futureState) {
        final int futureTime = futureState.time();
        for (int i = 0; i < toCheck.size(); i++) {
            if (toCheck.get(i).time() > futureTime) {
                toCheck.add(i, futureState);
                return;
            }
        }
        toCheck.add(futureState);
    }

    private static List<State> getAllChildStates(final State state, final Gate[] gates) {
        final int stateTime = state.time();

        final int position = state.position();
        if (position > -1 && !gates[position].isOpen(stateTime)) {
            return Collections.emptyList();
        }
        List<State> result = new ArrayList<>();
        if (position == -1) {
            final Gate gate = gates[0];
            if (!gate.isOpen(stateTime)) {
                return Collections.singletonList(new State(stateTime + getTimeUntil(gate, true, stateTime), position));
            } else {
                final int dt1 = getTimeUntil(gate, false, stateTime);
                final int time = stateTime + dt1;
                result.add(new State(time + getTimeUntil(gate, true, time), position));
            }
        } else {
            addWaitingMovesFor(gates[position], gates[position + 1], stateTime, position, result);
            if (position > 0) {
                addWaitingMovesFor(gates[position], gates[position - 1], stateTime, position, result);
            }
        }

        if (gates[position + 1].isOpen(stateTime)) {
            result.add(new State(stateTime, position + 1));
        }
        if (position > 0) {
            if (gates[position - 1].isOpen(stateTime)) {
                result.add(new State(stateTime, position - 1));
            }
        }
        return result;
    }

    private static void addWaitingMovesFor(final Gate currGate, final Gate neighbouringGate, final int time, final int position, final List<State> result) {
        int currTime = time;
        int timeSpan = getTimeUntil(currGate, false, currTime);
        while (timeSpan > 0) {
            final int timeUntilNextGateOpen = getTimeUntil(neighbouringGate, true, currTime);
            if (timeSpan > timeUntilNextGateOpen) {
                timeSpan -= timeUntilNextGateOpen;
                currTime += timeUntilNextGateOpen;
                result.add(new State(currTime, position));

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
