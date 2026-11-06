import java.util.ArrayList;

public class Season implements Comparable<Season> {
    private int seasonNum;
    private ArrayList<Episode> episodes;
    private Time totalTime;

    public Season(int seasonNum) {
        // TODO: assign seasonNum, create empty list, totalTime = new Time("00:00:00")
    }

    public String[] getEpisodeTitles() {
        // TODO: titles in the last sorted order (Menu 2 #1)
        return null;
    }

    public String getEpisodeInfo(int epIndex) {
        // TODO: (Menu 2 #2)
        return null;
    }

    public void watchEpisode(int epIndex) {
        // TODO: mark watched (Menu 2 #3)
    }

    public boolean addEpisode(String title, int epNum, String time) {
        // TODO: reject duplicate epNum, add, update totalTime (Menu 2 #4)
        return false;
    }

    public boolean removeByNumber(int epNum) {
        // TODO: remove, subtract length from totalTime (Menu 2 #5)
        return false;
    }

    public int removeByTitle(String title) {
        // TODO: remove ALL matches, return count removed
        return 0;
    }

    public int removeRange(int fromEp, int toEp) {
        // TODO: remove episode #s in range, return count removed
        return 0;
    }

    public int removeWatched() {
        // TODO: remove watched episodes, return count removed
        return 0;
    }

    public void sortByNumber() {
        // TODO: Collections.sort(episodes)
    }

    public void sortByTitle() {
        // TODO: Collections.sort(episodes, new EpisodeTitleComparator())
    }

    public void sortByTime() {
        // TODO: Collections.sort(episodes, new EpisodeTimeComparator())
    }

    public void updateTotalTime() {
        // TODO: recompute totalTime from episodes
    }

    public int getWatchedCount() {
        // TODO: used by TVShow.getStatus()
        return 0;
    }

    public int compareTo(Season other) {
        // TODO: compare by seasonNum
        return 0;
    }
}
