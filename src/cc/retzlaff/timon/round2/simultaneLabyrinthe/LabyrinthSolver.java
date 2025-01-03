package cc.retzlaff.timon.round2.simultaneLabyrinthe;


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

    public List<VectorMove> solveSimultaneously(final Labyrinths labyrinths) {
        labyrinths.generateDists();
        final int solveLab1Len = labyrinths.labyrinth1.getDist(labyrinths.labyrinth1.getStartPos());
        final int solveLab2Len = labyrinths.labyrinth2.getDist(labyrinths.labyrinth2.getStartPos());
        System.out.println("Lab1 Best way: " + solveLab1Len);
        System.out.println("Lab2 Best way: " + solveLab2Len);
        final int bestCase = Math.max(solveLab1Len, solveLab2Len);
        final int badCase = solveLab1Len + solveLab2Len;

        StateTracker tracker = new StateTracker();
        Queue<VectorScore> toCheck = new PriorityQueue<>(Comparator.comparingDouble(VectorScore::score));

        final Vector4 start = labyrinths.getStartPos();
        tracker.put(start, null);
        toCheck.add(new VectorScore(start, getScore(start, labyrinths), 0));
        Vector4 finish = labyrinths.getFinishPos();

        long steps = 0;
        boolean finishFound = false;
        while (!finishFound) {
            final VectorScore curr = toCheck.poll();
            final Vector4 vec = curr.vector();
            VectorMove[] possibleNextFields = labyrinths.getPossibleFields(vec);
            final int stepCount = curr.stepCount() + 1;
            for (VectorMove next : possibleNextFields) {
                if (!tracker.contains(next.vector())) {
                    final Vector4 nextVector = next.vector();
                    tracker.put(nextVector, new VectorMove(vec, next.move()));
                    toCheck.add(new VectorScore(nextVector, getScore(vec, labyrinths) + stepCount, stepCount));
                    if (finish.equals(nextVector)) {
                        finishFound = true;
                        break;
                    }
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

        List<VectorMove> result = new ArrayList<>();
        VectorMove curr = new VectorMove(finish, null);
        result.add(curr);
        while (true) {
            VectorMove next = tracker.get(curr.vector());
            if (next == null) {
                break;
            }
            result.add(next);
            curr = next;
        }
        Collections.reverse(result);
        return result;
    }

    private double getScore(final Vector4 pos, final Labyrinths labyrinths) {
        final int dist1 = labyrinths.labyrinth1.getDist(pos.x, pos.y);
        final int dist2 = labyrinths.labyrinth2.getDist(pos.z, pos.w);
        return Math.max(dist1, dist2) - 1d / (Math.min(dist1, dist2) + 1);
    }
}
