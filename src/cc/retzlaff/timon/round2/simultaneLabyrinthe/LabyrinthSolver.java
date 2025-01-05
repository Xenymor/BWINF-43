package cc.retzlaff.timon.round2.simultaneLabyrinthe;


import cc.retzlaff.timon.round2.simultaneLabyrinthe.Heuristics.Heuristic;

import java.util.*;

public class LabyrinthSolver {

    public List<Vector2> solve(final Labyrinth labyrinth) {
        Map<Vector2, Vector2> previous = new HashMap<>();
        Queue<Vector2> toCheck = new ArrayDeque<>();

        final Vector2 start = labyrinth.getStartPos();
        previous.put(start, null);
        toCheck.add(start);
        Vector2 finish = labyrinth.getFinishPos();

        long steps = 0;
        boolean finishFound = false;
        while (!finishFound) {
            final Vector2 curr = toCheck.poll();
            List<Vector2> possibleNextFields = labyrinth.getPossibleFields(curr);
            for (Vector2 next : possibleNextFields) {
                if (!previous.containsKey(next)) {
                    previous.put(next, curr);
                    toCheck.add(next);
                    if (finish.equals(next)) {
                        finishFound = true;
                        break;
                    }
                }
            }
            if (((steps++) & (1024 * 1024 - 1)) == 0) {
                System.out.println("Queue: " + toCheck.size() + " (" + steps + ")");
            }
        }

        List<Vector2> result = new ArrayList<>();
        Vector2 curr = finish;
        result.add(curr);
        while (true) {
            Vector2 next = previous.get(curr);
            if (next == null) {
                break;
            }
            result.add(next);
            curr = next;
        }
        Collections.reverse(result);
        return result;
    }

    public List<PositionData> solveSimultaneously(final Labyrinths labyrinths, Heuristic heuristic) {
        labyrinths.generateDists();
        final int solveLab1Len = labyrinths.labyrinth1.getDist(labyrinths.labyrinth1.getStartPos());
        final int solveLab2Len = labyrinths.labyrinth2.getDist(labyrinths.labyrinth2.getStartPos());
        System.out.println("Lab1 Best way: " + solveLab1Len);
        System.out.println("Lab2 Best way: " + solveLab2Len);
        if (Math.min(solveLab1Len, solveLab2Len) == 0) {
            System.out.println("There is no solution");
        }

        final int bestCase = Math.max(solveLab1Len, solveLab2Len);
        final int badCase = solveLab1Len + solveLab2Len;

        StateTracker tracker = new StateTracker();
        Queue<PositionData> toCheck = new PriorityQueue<>(Comparator.comparingDouble(PositionData::getScore));

        final Vector4 start = labyrinths.getStartPos();
        final PositionData positionData = new PositionData(start, heuristic.getScore(start, labyrinths), 0, null, Move.DOWN);
        tracker.put(start, positionData);
        toCheck.add(positionData);
        Vector4 finish = labyrinths.getFinishPos();
        PositionData finishData = null;

        long steps = 0;
        boolean finishFound = false;
        while (!finishFound) {
            final PositionData curr = toCheck.poll();
            final Vector4 vec = curr.getVector();
            if (tracker.get(vec) == null) {
                continue;
            }
            if (vec.equals(finish)) {
                finishFound = true;
                finishData = curr;
                continue;
            }
            tracker.removeFromMap(vec);
            VectorMove[] possibleNextFields = labyrinths.getPossibleFields(vec);
            final int stepCount = curr.getStepCount() + 1;
            for (VectorMove next : possibleNextFields) {
                if (tracker.hasSeen(next.vector())) {
                    final PositionData oldPositionData = tracker.get(next.vector());
                    if (oldPositionData != null && oldPositionData.getStepCount() > stepCount) {
                        final PositionData nextScore = new PositionData(next.vector(), heuristic.getScore(next.vector(), labyrinths) + stepCount, stepCount, curr, next.move());
                        tracker.put(next.vector(), nextScore);
                        toCheck.add(nextScore);
                    }
                } else {
                    final Vector4 nextVector = next.vector();
                    final PositionData nextScore = new PositionData(nextVector, heuristic.getScore(nextVector, labyrinths) + stepCount, stepCount, curr, next.move());
                    tracker.put(nextVector, nextScore);
                    toCheck.add(nextScore);
                }
            }
            if (((steps++) & (1024 * 1024 - 1)) == 0) {
                final PositionData top = toCheck.peek();
                System.out.println(
                        "Queue: bestWayLen = " + top.getStepCount() + " + " + (top.getScore() - top.getStepCount())
                                + " Progress: " + ((int) ((top.getScore() - bestCase) * 10000 / (badCase - bestCase))) / 100F + "%"
                                + " // queueLen = " + toCheck.size() + " (" + steps + ")"
                );
            }
        }

        final List<PositionData> path = getPath(finishData);
        for (int i = 0; i < path.size(); i++) {
            final PositionData vectorMove = path.get(i);
            final Vector4 field = vectorMove.getVector();
            if (heuristic.getScore(field, labyrinths) > path.size() - i - 1) {
                System.out.println("Alarm");
            }
        }
        return path;
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
