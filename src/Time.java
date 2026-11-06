public class Time implements Comparable<Time> {
    private int hours;
    private int minutes;
    private int seconds;

    public Time(String hhmmss) {
        // TODO: split on ":" and parse hours, minutes, seconds
    }

    public void add(Time other) {
        // TODO: add each field, carry seconds -> minutes -> hours
    }

    public void subtract(Time other) {
        // TODO: subtract each field, borrow as needed (used when removing episodes)
    }

    public int compareTo(Time other) {
        // TODO: compare hours, then minutes, then seconds
        return 0;
    }

    public String toString() {
        // TODO: format as hh:mm:ss
        return "";
    }
}
