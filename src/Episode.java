public class Episode implements Comparable<Episode> {
    private String title;
    private int episodeNum;
    private boolean watched;
    private Time length;

    public Episode(String title, int episodeNum, Time length) {
        // TODO: assign fields, watched = false
    }

    public int compareTo(Episode other) {
        // TODO: compare by episode number
        return 0;
    }

    public String toString() {
        // TODO: episode #, title, length, watched status
        return "";
    }

    public String getTitle() {
        // TODO
        return null;
    }

    public Time getLength() {
        // TODO
        return null;
    }
}
