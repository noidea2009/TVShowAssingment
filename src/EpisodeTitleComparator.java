import java.util.Comparator;

public class EpisodeTitleComparator implements Comparator<Episode> {

    /**
     * Compares the title of 2 episodes, returns a compareto function output
     */

    public EpisodeTitleComparator() {
    }

    public int compare(Episode a, Episode b) {
         return a.getTitle().compareToIgnoreCase(b.getTitle());
    }
}
