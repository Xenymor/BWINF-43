package cc.retzlaff.timon.round2.simultaneLabyrinthe.base;


import cc.retzlaff.timon.round2.simultaneLabyrinthe.base.Heuristics.Heuristic;

import java.util.*;

public class LabyrinthSolver {

    public List<PositionData> solveSimultaneously(final Labyrinths labyrinths, Heuristic heuristic) {
        labyrinths.generateDists();
        final int solveLab1Len = labyrinths.labyrinth1.getDist(labyrinths.labyrinth1.getStartPos3D());
        final int solveLab2Len = labyrinths.labyrinth2.getDist(labyrinths.labyrinth2.getStartPos3D());
        System.out.println("Lab1 Best way: " + solveLab1Len);
        System.out.println("Lab2 Best way: " + solveLab2Len);
        if (labyrinths.startJumpCount > 0) {
            System.out.println("Start jump count: " + labyrinths.startJumpCount);
        }
        if (Math.min(solveLab1Len, solveLab2Len) == 0) {
            System.out.println("There is no solution");
            return new ArrayList<>();
        }

        final int bestCase = Math.max(solveLab1Len, solveLab2Len);
        final int badCase = solveLab1Len + solveLab2Len;

        StateTracker tracker = new StateTracker();
        Queue<PositionData> toCheck = new PriorityQueue<>(Comparator.comparingDouble(PositionData::getScore));

        final State start = labyrinths.getStartPos();
        final PositionData positionData = new PositionData(start, heuristic.getScore(start, labyrinths), 0, null, Move.DOWN);
        tracker.put(start, positionData);
        toCheck.add(positionData);
        State finish = labyrinths.getFinishPos();
        PositionData finishData = null;

        long steps = 0;
        boolean finishFound = false;
        while (!finishFound) {
            final PositionData curr = toCheck.poll();
            final State state = curr.getState();
            if (tracker.get(state) == null) {
                continue;
            }
            if (state.equalsIgnoreJumpCount(finish)) {
                finishFound = true;
                finishData = curr;
                continue;
            }
            tracker.removeFromMap(state);
            StateMove[] possibleNextFields = labyrinths.getPossibleFields(state);
            final int stepCount = curr.getStepCount() + 1;
            for (StateMove next : possibleNextFields) {
                if (tracker.hasSeen(next.state())) {
                    final PositionData oldPositionData = tracker.get(next.state());
                    if (oldPositionData != null && oldPositionData.getStepCount() > stepCount) {
                        final PositionData nextScore = new PositionData(next.state(), heuristic.getScore(next.state(), labyrinths) + stepCount, stepCount, curr, next.move());
                        tracker.put(next.state(), nextScore);
                        toCheck.add(nextScore);
                    }
                } else {
                    final State nextState = next.state();
                    final PositionData nextScore = new PositionData(nextState, heuristic.getScore(nextState, labyrinths) + stepCount, stepCount, curr, next.move());
                    tracker.put(nextState, nextScore);
                    toCheck.add(nextScore);
                }
            }
            if (((steps++) & (1024 * 4096 - 1)) == 0) {
                final PositionData top = toCheck.peek();
                System.out.println(
                        "Queue: bestWayLen = " + top.getStepCount() + " + " + (top.getScore() - top.getStepCount())
                                + " Progress: " + ((int) ((top.getScore() - bestCase) * 10000 / (badCase - bestCase))) / 100F + "%"
                                + " // queueLen = " + toCheck.size() + " (" + steps + ")"
                                + " // mapSize = " + tracker.getMapSize()
                );
            }
        }

        return getPath(finishData);
    }

    private List<PositionData> getPath(final PositionData finish) {
        List<PositionData> result = new ArrayList<>();
        PositionData curr = finish;
        result.add(curr);
        while (true) {
            PositionData next = curr.previous;
            if (next == null) {
                break;
            }
            result.add(next);
            curr = next;
        }
        Collections.reverse(result);
        return result;
    }
}
