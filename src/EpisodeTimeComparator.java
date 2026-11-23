import java.util.Comparator;

public class EpisodeTimeComparator implements Comparator<Episode> {

    /**
     *Compares the time of 2 episodes, returns a compareto function output
     *
     */

    public EpisodeTimeComparator() {
    }

    public int compare(Episode a, Episode b) {

        return a.getLength().compareTo(b.getLength());
    }
}
