package cc.retzlaff.timon.wandertag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

        Arrays.sort(values);
        values = removeDoubledValues(values);
        int[] indices = new int[PATH_COUNT];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        int bestCount = -1;
        int[] bestIndices = new int[PATH_COUNT];

        long start = System.nanoTime();
        while (indices[0] <= values.length - PATH_COUNT) {
            int currCount = getCount(indices, values, persons);
            if (currCount > bestCount) {
                bestCount = currCount;
                bestIndices = Arrays.copyOf(indices, indices.length);
            }
            for (int i = indices.length - 1; i >= 0; i--) {
                indices[i]++;
                if (indices[i] >= values.length - (indices.length - i)) {
                    if (i == 0) {
                        break;
                    }
                    indices[i] = indices[i - 1] + 2;
                } else {
                    break;
                }
            }
        }
        long end = System.nanoTime();

        StringBuilder solution = new StringBuilder();
        solution.append(bestCount).append(" people attend for the lengths: ");
        for (final int index : bestIndices) {
            solution.append(values[index]).append("m; ");
        }
        solution.replace(solution.length() - 2, solution.length(), "");
        System.out.println(solution);
        System.out.println("Found in " + (end - start) / 1_000_000 + "ms");
    }

    public static int[] removeDoubledValues(int[] values) {
        List<Integer> resultList = new ArrayList<>();
        int i = 0;

        while (i < values.length) {
            resultList.add(values[i]);

            int current = values[i];
            while (i < values.length && values[i] == current) {
                i++;
            }
        }

        return resultList.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int getCount(final int[] indices, final int[] values, final Person[] persons) {
        for (Person person : persons) {
            person.included = false;
        }
        int count = 0;
        for (int index : indices) {
            int length = values[index];
            for (Person person : persons) {
                if (!person.included && length >= person.min && length <= person.max) {
                    count++;
                    person.included = true;
                }
            }
        }
        return count;
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
