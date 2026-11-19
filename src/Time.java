/**
 * Time.java
 * Name: Junjit Chang
 *
 * A Time object stores a length of time as hours, minutes and seconds.
 * Times can be compared, added together and subtracted from each other.
 */
public class Time implements Comparable<Time> {
    /** The number of hours. */
    private int hours;
    /** The number of minutes (0 to 59). */
    private int minutes;
    /** The number of seconds (0 to 59). */
    private int seconds;

    /**
     * Creates a time from text in the format hh:mm:ss.
     *
     * @param hhmmss the time as text (for example "00:47:23"); assumed to be valid
     */
    public Time(String hhmmss) {
        // Split the text into hours, minutes and seconds
        String[] parts = hhmmss.trim().split(":");
        this.hours = Integer.parseInt(parts[0]);
        this.minutes = Integer.parseInt(parts[1]);
        this.seconds = Integer.parseInt(parts[2]);
    }

    /**
     * Adds another time to this time, carrying seconds into minutes
     * and minutes into hours.
     *
     * @param other the time to add (it is not changed)
     */
    public void add(Time other) {
        // Add each field separately
        seconds += other.seconds;
        minutes += other.minutes;
        hours += other.hours;

        // Carry seconds -> minutes -> hours
        minutes += seconds / 60;
        seconds = seconds % 60;
        hours += minutes / 60;
        minutes = minutes % 60;
    }

    /**
     * Subtracts another time from this time, borrowing from the larger
     * fields as needed. If the other time is longer than this one, this
     * time becomes 00:00:00.
     *
     * @param other the time to subtract (it is not changed)
     */
    public void subtract(Time other) {
        // Never go below zero
        if (compareTo(other) <= 0) {
            hours = 0;
            minutes = 0;
            seconds = 0;
            return;
        }

        // Subtract each field separately
        seconds -= other.seconds;
        minutes -= other.minutes;
        hours -= other.hours;

        // Borrow a minute for the seconds, and an hour for the minutes
        if (seconds < 0) {
            seconds += 60;
            minutes--;
        }
        if (minutes < 0) {
            minutes += 60;
            hours--;
        }
    }

    /**
     * Compares this time to another: hours first, then minutes, then seconds.
     *
     * @param other the time to compare to
     * @return a negative number if this time is shorter, 0 if they are equal,
     *         or a positive number if this time is longer
     */
    public int compareTo(Time other) {
        if (hours != other.hours) {
            return Integer.compare(hours, other.hours);
        }
        if (minutes != other.minutes) {
            return Integer.compare(minutes, other.minutes);
        }
        return Integer.compare(seconds, other.seconds);
    }

    /**
     * Formats the time as hh:mm:ss.
     *
     * @return the time as text
     */
    public String toString() {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}