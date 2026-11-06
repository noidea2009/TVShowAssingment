import java.util.ArrayList;

public class TVShow {
    private String title;
    private String genre;
    private ArrayList<Season> seasons;
    private Time totalTime;
    private static int numShows;

    public TVShow(String title, String genre) {
        // TODO: assign fields, create list, totalTime = 0, numShows++
    }

    public boolean addSeason(int seasonNum) {
        // TODO: reject duplicate season #, keep seasons sorted
        return false;
    }

    public boolean removeSeason(int seasonNum) {
        // TODO: remove, then updateTotalTime()
        return false;
    }

    public Season getSeason(int seasonNum) {
        // TODO: Collections.binarySearch if seasons kept sorted by season #
        return null;
    }

    public String getInfo() {
        // TODO: title, genre, # seasons, # episodes, total time
        return null;
    }

    public String getStatus() {
        // TODO: per-season watched/total, # seasons fully watched, # unwatched
        return null;
    }

    public int getNumEpisodes() {
        // TODO: sum over seasons
        return 0;
    }

    public void updateTotalTime() {
        // TODO: sum each season's totalTime
    }

    public static int getNumShows() {
        return numShows;
    }

    public boolean addEpisode(int seasonNum, String title, int epNum, String time) {
        // TODO: getSeason(...).addEpisode(...), then updateTotalTime()
        return false;
    }

    public boolean removeEpisodeByNumber(int seasonNum, int epNum) {
        // TODO: Season.removeByNumber, then updateTotalTime()
        return false;
    }

    public int removeEpisodesByTitle(int seasonNum, String title) {
        // TODO: Season.removeByTitle, then updateTotalTime()
        return 0;
    }

    public int removeEpisodeRange(int seasonNum, int fromEp, int toEp) {
        // TODO: Season.removeRange, then updateTotalTime()
        return 0;
    }

    public int removeWatchedEpisodes(int seasonNum) {
        // TODO: Season.removeWatched, then updateTotalTime()
        return 0;
    }
}
