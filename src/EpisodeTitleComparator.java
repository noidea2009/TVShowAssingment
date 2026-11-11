import java.util.Comparator;

public class EpisodeTitleComparator implements Comparator<Episode> {

    public EpisodeTitleComparator() {
    }

    public int compare(Episode a, Episode b) {
         return a.getTitle().compareToIgnoreCase(b.getTitle());
    }
}
