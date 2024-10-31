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
        int[] values = new int[count * 2];
        Person[] persons = new Person[count];
        initializeArrays(lines, values, persons);

        if (persons.length <= PATH_COUNT) {
            StringBuilder solution = new StringBuilder();
            solution.append(persons.length).append(" people attend for the lengths: ");
            for (final Person person : persons) {
                solution.append(person.min).append("m; ");
            }
            if (persons.length > 0) {
                solution.replace(solution.length() - 2, solution.length(), "");
            }
            System.out.println(solution);
            System.exit(0);
        }

        values = removeDoubledValues(values);
        Arrays.sort(values);
        int bestCount = -1;
        int[] bestIndices = new int[PATH_COUNT];

        long start = System.nanoTime();
        for (int i = 0; i < persons.length - 2; i++) {
            for (int j = i + 1; j < persons.length - 1; j++) {
                for (int k = j + 1; k < persons.length; k++) {
                    int currCount = getCount(values[i], values[j], values[k], persons);
                    if (currCount > bestCount) {
                        bestCount = currCount;
                        bestIndices[0] = i;
                        bestIndices[1] = j;
                        bestIndices[2] = k;
                    }
                }
            }
        }

        long end = System.nanoTime();

        StringBuilder solution = new StringBuilder();
        solution.append(bestCount).

                append(" people attend for the lengths: ");
        for (
                final int index : bestIndices) {
            solution.append(values[index]).append("m; ");
        }
        solution.replace(solution.length() - 2, solution.length(), "");
        System.out.println(solution);
        System.out.println("Found in " + (end - start) / 1_000_000 + "ms");
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
        int min = person.min;
        int max = person.max;
        return (min <= length1 && max >= length1)
                || (min <= length2 && max >= length2)
                || (min <= length3 && max >= length2);
    }

    private static void initializeArrays(final List<String> lines, final int[] values, final Person[] persons) {
        for (int i = 0; i < persons.length; i++) {
            String line = lines.get(i + 1);
            String[] parts = line.split(" ");
            int min = Integer.parseInt(parts[0]);
            int max = Integer.parseInt(parts[1]);
            values[i * 2] = min;
            values[i * 2 + 1] = max;
            persons[i] = new Person(min, max);
        }
    }
}
