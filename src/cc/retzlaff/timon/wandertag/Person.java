package cc.retzlaff.timon.wandertag;

public class Person {
    final int min;
    final int max;
    boolean included = false;

    public Person(final int min, final int max) {
        this.min = min;
        this.max = max;
    }
}
