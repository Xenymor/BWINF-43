package cc.retzlaff.timon.wandertag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Main {
    static final int PATH_COUNT = 3;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(args[0]));

        final int count = Integer.parseInt(lines.get(0));
        int[] values = new int[count];
        Person[] persons = new Person[count];
        initializeArrays(lines, values, persons);

        if (persons.length <= PATH_COUNT) {
            printEasySolution(persons);
            return;
        }

        values = removeDoubledValues(values);
        Arrays.sort(values);

        int[][] countTable = new int[values.length][values.length];
        initializeTable(countTable, values, persons);

        int bestCount = -1;
        int[] bestLengths = new int[PATH_COUNT];

        long start = System.nanoTime();
        for (int i = 0; i < values.length - 2; i++) {
            int localBest = 0;
            for (int j = i + 1; j < values.length - 1; j++) {
                for (int k = j + 1; k < values.length; k++) {
                    localBest = Math.max(countTable[j][k], localBest);
                }
            }
            if (getCount(values[i], persons)+localBest < bestCount) {
                continue;
            }
            for (int j = i + 1; j < values.length - 1; j++) {
                for (int k = j + 1; k < values.length; k++) {
                    int currCount = getCount(values[i], values[j], values[k], persons);
                    if (currCount > bestCount) {
                        bestCount = currCount;
                        bestLengths[0] = values[i];
                        bestLengths[1] = values[j];
                        bestLengths[2] = values[k];
                    }
                }
            }
        }
        long end = System.nanoTime();

        printResult(bestCount, bestLengths, end - start);
    }

    private static int getCount(final int length1, final Person[] persons) {
        int count = 0;
        for (Person person : persons) {
            if (isParticipating(person, length1)) {
                count++;
            }
        }
        return count;

    }

    private static boolean isParticipating(final Person person, final int length1) {
        return isInRange(person, length1);
    }

    private static void initializeTable(final int[][] countTable, final int[] values, final Person[] persons) {
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < i; j++) {
                int count = getCount(values[i], values[j], persons);
                countTable[i][j] = count;
                countTable[j][i] = count;
            }
        }
    }

    private static int getCount(final int length1, final int length2, final Person[] persons) {
        int count = 0;
        for (Person person : persons) {
            if (isParticipating(person, length1, length2)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isParticipating(final Person person, final int length1, final int length2) {
        return isInRange(person, length1) || isInRange(person, length2);
    }

    public static int[] removeDoubledValues(int[] values) {
        HashSet<Integer> resultSet = new HashSet<>();
        for (int value : values) {
            resultSet.add(value);
        }
        return resultSet.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int getCount(final int length1, final int length2, final int length3, final Person[] persons) {
        int count = 0;
        for (Person person : persons) {
            if (isParticipating(person, length1, length2, length3)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isParticipating(final Person person, final int length1, final int length2, final int length3) {
        return isInRange(person, length1) || isInRange(person, length2) || isInRange(person, length3);
    }

    private static boolean isInRange(Person person, int length) {
        return person.min <= length && person.max >= length;
    }

    private static void initializeArrays(final List<String> lines, final int[] values, final Person[] persons) {
        for (int i = 0; i < persons.length; i++) {
            String line = lines.get(i + 1);
            String[] parts = line.split(" ");
            int min = Integer.parseInt(parts[0]);
            int max = Integer.parseInt(parts[1]);
            values[i] = min;
            persons[i] = new Person(min, max);
        }
    }

    private static void printEasySolution(Person[] persons) {
        StringBuilder solution = new StringBuilder();
        solution.append(persons.length).append(" people attend for the lengths: ");
        for (final Person person : persons) {
            solution.append(person.min).append("m; ");
        }
        if (persons.length > 0) {
            solution.replace(solution.length() - 2, solution.length(), "");
        }
        System.out.println(solution);
    }

    private static void printResult(int bestCount, int[] bestLengths, long duration) {
        StringBuilder solution = new StringBuilder();
        solution.append(bestCount).append(" people attend for the lengths: ");
        for (int length : bestLengths) {
            solution.append(length).append("m; ");
        }
        solution.replace(solution.length() - 2, solution.length(), "");
        System.out.println(solution);
        System.out.println("Found in " + duration / 1_000_000 + "ms");
    }
}
