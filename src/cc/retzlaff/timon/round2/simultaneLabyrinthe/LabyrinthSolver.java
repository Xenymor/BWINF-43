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
        Map<Vector4, VectorMove> previous = new HashMap<>();
        Queue<Vector4> toCheck = new ArrayDeque<>();

        final Vector4 start = labyrinths.getStartPos();
        previous.put(start, null);
        toCheck.add(start);
        Vector4 finish = labyrinths.getFinishPos();

        long steps = 0;
        boolean finishFound = false;
        while (!finishFound) {
            final Vector4 curr = toCheck.poll();
            VectorMove[] possibleNextFields = labyrinths.getPossibleFields(curr);
            for (VectorMove next : possibleNextFields) {
                if (!previous.containsKey(next.vector())) {
                    final Vector4 nextVector = next.vector();
                    previous.put(nextVector, new VectorMove(curr, next.move()));
                    toCheck.add(nextVector);
                    if (finish.equals(nextVector)) {
                        finishFound = true;
                        break;
                    }
                }
            }
            if (((steps++)&(1024*1024-1)) == 0) {
                System.out.println("Queue: " + toCheck.size() + " (" + steps + ")");
            }
        }

        List<VectorMove> result = new ArrayList<>();
        VectorMove curr = new VectorMove(finish, null);
        result.add(curr);
        while (true) {
            VectorMove next = previous.get(curr.vector());
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
