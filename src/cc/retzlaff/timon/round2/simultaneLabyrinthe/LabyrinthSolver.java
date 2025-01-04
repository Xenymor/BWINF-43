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

    public List<VectorMove> solveSimultaneously(final Labyrinths labyrinths, Heuristic heuristic, List<VectorMove> debug) {
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
        Queue<VectorScore> toCheck = new PriorityQueue<>(Comparator.comparingDouble(VectorScore::score));

        final Vector4 start = labyrinths.getStartPos();
        tracker.put(start, new VectorMove(start, Move.DOWN, 0));
        toCheck.add(new VectorScore(start, heuristic.getScore(start, labyrinths), 0));
        Vector4 finish = labyrinths.getFinishPos();

        long steps = 0;
        boolean finishFound = false;
        while (!finishFound) {
            final VectorScore curr = toCheck.poll();
            final Vector4 vec = curr.vector();
            if (vec.equals(finish)) {
                finishFound = true;
                continue;
            }
            VectorMove[] possibleNextFields = labyrinths.getPossibleFields(vec);
            final int stepCount = curr.stepCount() + 1;
            for (VectorMove next : possibleNextFields) {
                if (tracker.contains(next.vector())) {
                    final VectorMove vectorMove = tracker.get(next.vector());
                    if (vectorMove.stepCount() > stepCount) {
                        tracker.put(next.vector(), new VectorMove(vec, next.move(), stepCount));
                        toCheck.add(new VectorScore(next.vector(), heuristic.getScore(next.vector(), labyrinths) + stepCount, stepCount));
                    }
                } else {
                    final Vector4 nextVector = next.vector();
                    tracker.put(nextVector, new VectorMove(vec, next.move(), stepCount));
                    toCheck.add(new VectorScore(nextVector, heuristic.getScore(nextVector, labyrinths) + stepCount, stepCount));
                }
            }
            if (((steps++) & (1024 * 1024 - 1)) == 0) {
                final VectorScore top = toCheck.peek();
                System.out.println(
                        "Queue: bestWayLen = " + top.stepCount() + " + " + (top.score() - top.stepCount())
                                + " Progress: " + ((int) ((top.score() - bestCase) * 10000 / (badCase - bestCase))) / 100F + "%"
                                + " // queueLen = " + toCheck.size() + " (" + steps + ")"
                );
            }
        }

        /*if (debug != null) {
            Collections.reverse(debug);
            for (VectorMove vectorMove : debug) {
                final Vector4 vector = vectorMove.vector();
                for (VectorScore vectorScore : toCheck) {
                    if (vectorScore.vector().equals(vector)) {
                        VectorMove move = tracker.get(vector);
                        double score = heuristic.getScore(vector, labyrinths);
                        System.out.println(vector + ": " + move.stepCount() + " + " + score + " = " + (score + move.stepCount()) + " \\\\ " + labyrinths.getLabyrinth1().getDist(vector.x, vector.y) + "; " + labyrinths.getLabyrinth2().getDist(vector.z, vector.w));
                        break;
                    }
                }
            }
            Collections.reverse(debug);
        }*/

        final List<VectorMove> path = getPath(tracker, finish);
        for (int i = 0; i < path.size(); i++) {
            final VectorMove vectorMove = path.get(i);
            final Vector4 field = vectorMove.vector();
            if (heuristic.getScore(field, labyrinths) > path.size() - i - 1) {
                System.out.println("Alarm");
            }
        }
        return path;
    }

    private List<VectorMove> getPath(final StateTracker tracker, final Vector4 finish) {
        List<VectorMove> result = new ArrayList<>();
        VectorMove curr = new VectorMove(finish, null, 0);
        result.add(curr);
        while (true) {
            VectorMove next = tracker.get(curr.vector());
            if (next.vector().equals(curr.vector())) {
                break;
            }
            result.add(next);
            curr = next;
        }
        Collections.reverse(result);
        return result;
    }
}
