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

    //TODO stop moving when finished
    public List<Vector4> solveSimultaneously(final Labyrinths labyrinths) {
        Map<Vector4, Vector4> previous = new HashMap<>();
        Queue<Vector4> toCheck = new ArrayDeque<>();

        final Vector4 start = labyrinths.getStartPos();
        previous.put(start, null);
        toCheck.add(start);
        Vector4 finish = labyrinths.getFinishPos();

        boolean finishFound = false;
        while (!finishFound) {
            final Vector4 curr = toCheck.poll();
            Vector4[] possibleNextFields = labyrinths.getPossibleFields(curr);
            for (Vector4 next : possibleNextFields) {
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

        List<Vector4> result = new ArrayList<>();
        Vector4 curr = finish;
        result.add(curr);
        while (true) {
            Vector4 next = previous.get(curr);
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
