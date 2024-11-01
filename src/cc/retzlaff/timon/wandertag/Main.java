package cc.retzlaff.timon.wandertag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;

public class Main {
    static final int PATH_COUNT = 3;
    static int doubledCount = 0;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(args[0]));
        long start = System.nanoTime();

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

        BitSet[] bitSets = initializeBitSets(values, persons);

        int bestCount = -1;
        int[] bestLengths = new int[PATH_COUNT];

        BitSet preComputed = new BitSet();
        for (int i = 0; i < values.length - 2; i++) {
            for (int j = i + 1; j < values.length - 1; j++) {
                preComputed.clear();
                preComputed.or(bitSets[i]);
                preComputed.or(bitSets[j]);
                for (int k = j + 1; k < values.length; k++) {
                    int currCount = getCount(preComputed, k, bitSets);
                    if (currCount > bestCount) {
                        bestCount = currCount;
                        bestLengths[0] = values[i];
                        bestLengths[1] = values[j];
                        bestLengths[2] = values[k];
                    } else if (currCount + (values.length - k - 1) + doubledCount <= bestCount) {
                        break;
                    }
                }
            }
        }
        long end = System.nanoTime();

        printResult(bestCount, bestLengths, end - start);
    }

    private static BitSet[] initializeBitSets(final int[] values, final Person[] persons) {
        BitSet[] result = new BitSet[values.length];
        for (int j = 0; j < values.length; j++) {
            final int value = values[j];
            BitSet curr = new BitSet();
            for (int i = 0; i < persons.length; i++) {
                Person person = persons[i];
                curr.set(i, isInRange(person, value));
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

    static BitSet bitSet = new BitSet();

    private static int getCount(BitSet preComputed, final int k, final BitSet[] bitSets) {
        bitSet.clear();
        bitSet.or(preComputed);
        bitSet.or(bitSets[k]);
        return bitSet.cardinality();
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
