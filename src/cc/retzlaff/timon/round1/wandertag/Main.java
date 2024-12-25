package cc.retzlaff.timon.round1.wandertag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Main {
    static final int PATH_COUNT = 3;
    static int doubledCount = 0;
    static int bitsetLength;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(args[0]));
        long start = System.nanoTime();
        int bestCount = -1;
        int[] bestLengths = new int[PATH_COUNT];
        final int count = Integer.parseInt(lines.get(0));
        int[] values = new int[count];
        Person[] persons = new Person[count];
        initializeArrays(lines, values, persons);

        values = removeDoubledValues(values);
        Arrays.sort(values);

        if (values.length <= PATH_COUNT) {
            printEasySolution(persons, values);
            return;
        }

        MyBitSet[] bitSets = initializeBitSets(values, persons);
        final MyBitSet preComputed = new MyBitSet(bitsetLength);

        int len = values.length;
        for (int i = 0; i < len - 2; i++) {
            for (int j = len - 2; j >= i + 1; j--) {
                preComputed.copyOr(bitSets[i], bitSets[j]);
                for (int k = j + 1; k < len; k++) {
                    int currCount = preComputed.orCardinality(bitSets[k]);
                    if (currCount > bestCount) {
                        bestCount = currCount;
                        bestLengths[0] = values[i];
                        bestLengths[1] = values[j];
                        bestLengths[2] = values[k];
                    } else if (currCount + (len - k - 1) + doubledCount <= bestCount) {
                        break;
                    }
                }
            }
        }
        long end = System.nanoTime();

        printResult(bestCount, bestLengths, (double) (end - start));
    }

    private static void printEasySolution(final Person[] persons, final int[] values) {
        StringBuilder solution = new StringBuilder();
        solution.append(persons.length).append(" people attend for the lengths: ");
        for (final int value : values) {
            solution.append(value).append("m; ");
        }
        if (persons.length > 0) {
            solution.replace(solution.length() - 2, solution.length(), "");
        }
        System.out.println(solution);
    }

    private static MyBitSet[] initializeBitSets(final int[] values, final Person[] persons) {
        MyBitSet[] result = new MyBitSet[values.length];
        int length = persons.length / 64 + ((persons.length & 63) == 0 ? 0 : 1);
        bitsetLength = length;
        for (int j = 0; j < values.length; j++) {
            final int value = values[j];
            MyBitSet curr = new MyBitSet(length);
            for (int i = 0; i < persons.length; i++) {
                Person person = persons[i];
                final boolean inRange = isInRange(person, value);
                if (inRange) {
                    curr.set(i);
                }
            }
            result[j] = curr;
        }
        return result;
    }

    public static int[] removeDoubledValues(int[] values) {
        HashSet<Integer> resultSet = new HashSet<>();
        for (int value : values) {
            resultSet.add(value);
        }
        doubledCount = values.length - resultSet.size();
        return resultSet.stream().mapToInt(Integer::intValue).toArray();
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

    private static void printResult(int bestCount, int[] bestLengths, double duration) {
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
