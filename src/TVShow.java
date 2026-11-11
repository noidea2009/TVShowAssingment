import java.util.ArrayList;
import java.util.Collections;

/**
 * TVShow.java
 * Name: (your name)
 *
 * A TVShow has a title, a genre, a list of Season objects (kept sorted by season
 * number so they can be binary searched) and the total length of all of its
 * episodes. A class variable keeps track of how many TV shows are in the collection.
 */
public class TVShow {
    /** The title of the show. */
    private String title;
    /** The genre of the show. */
    private String genre;
    /** The seasons of the show, always sorted by season number. */
    private ArrayList<Season> seasons;
    /** The total length of all the episodes in all the seasons. */
    private Time totalTime;
    /** The number of TV shows in the collection. */
    private static int numShows;

    /**
     * Creates a TV show with no seasons and counts it in the collection.
     *
     * @param title the title of the show
     * @param genre the genre of the show
     */
    public TVShow(String title, String genre) {
        this.title = title;
        this.genre = genre;
        this.seasons = new ArrayList<Season>();
        this.totalTime = new Time("00:00:00");
        numShows++;
    }

    /**
     * Gets the title of the show.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Gets the genre of the show.
     *
     * @return the genre
     */
    public String getGenre() {
        return this.genre;
    }

    /**
     * Gets the number of seasons in the show.
     *
     * @return the number of seasons
     */
    public int getNumSeasons() {
        return seasons.size();
    }

    /**
     * Finds the position of a season in the (sorted) list using a binary search.
     *
     * @param seasonNum the season number to look for
     * @return the index of the season if found, otherwise a negative number
     *         (-(insertion point) - 1), as returned by Collections.binarySearch
     */
    private int findSeasonIndex(int seasonNum) {
        // A temporary Season is only used as the search key (compared by season #)
        return Collections.binarySearch(seasons, new Season(seasonNum));
    }

    /**
     * Adds a new, empty season. Duplicate season numbers are rejected, and the
     * list of seasons is kept sorted by season number.
     *
     * @param seasonNum the number of the new season
     * @return true if the season was added, false if the number is invalid or already exists
     */
    public boolean addSeason(int seasonNum) {
        if (seasonNum < 1) {
            return false;
        }
        int index = findSeasonIndex(seasonNum);
        if (index >= 0) {
            return false; // duplicate season number
        }
        // A negative result holds the position where the season belongs
        seasons.add(-index - 1, new Season(seasonNum));
        return true;
    }

    /**
     * Removes a season (and all of its episodes) and updates the total time.
     *
     * @param seasonNum the number of the season to remove
     * @return true if the season was removed, false if it does not exist
     */
    public boolean removeSeason(int seasonNum) {
        int index = findSeasonIndex(seasonNum);
        if (index < 0) {
            return false;
        }
        seasons.remove(index);
        updateTotalTime();
        return true;
    }

    /**
     * Gets a season using a binary search.
     *
     * @param seasonNum the season number to look for
     * @return the Season object, or null if the season does not exist
     */
    public Season getSeason(int seasonNum) {
        int index = findSeasonIndex(seasonNum);
        if (index < 0) {
            return null;
        }
        return seasons.get(index);
    }

    /**
     * Gets the numbers of all the seasons, in order. The position in the array
     * matches the position in the array returned by getSeasonLabels().
     *
     * @return an array of season numbers
     */
    public int[] getSeasonNumbers() {
        int[] numbers = new int[seasons.size()];
        for (int i = 0; i < seasons.size(); i++) {
            numbers[i] = seasons.get(i).getSeasonNum();
        }
        return numbers;
    }

    /**
     * Gets a short description of each season (for example "Season 2 (11 episodes)"),
     * in the same order as getSeasonNumbers().
     *
     * @return an array of season descriptions
     */
    public String[] getSeasonLabels() {
        String[] labels = new String[seasons.size()];
        for (int i = 0; i < seasons.size(); i++) {
            Season season = seasons.get(i);
            int count = season.getNumEpisodes();
            labels[i] = "Season " + season.getSeasonNum() + " (" + count
                    + (count == 1 ? " episode)" : " episodes)");
        }
        return labels;
    }

    /**
     * Gets the information about the show: title, genre, number of seasons,
     * number of episodes and the total time of all the episodes.
     *
     * @return the information as multi-line text
     */
    public String getInfo() {
        return "Title: " + title + "\n"
                + "Genre: " + genre + "\n"
                + "Number of seasons: " + seasons.size() + "\n"
                + "Number of episodes: " + getNumEpisodes() + "\n"
                + "Total time: " + totalTime;
    }

    /**
     * Gets how much of the show has been watched: the watched count of each
     * season, the number of seasons completely watched and the number of
     * episodes still unwatched.
     *
     * @return the status as multi-line text
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Watch status of \"").append(title).append("\"\n\n");

        int seasonsWatched = 0;
        int unwatched = 0;

        // Go through each season, adding up the totals as we list it
        for (Season season : seasons) {
            int watched = season.getWatchedCount();
            int total = season.getNumEpisodes();
            sb.append("Season ").append(season.getSeasonNum()).append(" = ")
                    .append(watched).append(" out of ").append(total)
                    .append(" eps watched\n");

            // An empty season does not count as completely watched
            if (total > 0 && watched == total) {
                seasonsWatched++;
            }
            unwatched += total - watched;
        }

        if (seasons.isEmpty()) {
            sb.append("This show has no seasons yet.\n");
        }
        sb.append("\nSeasons completely watched: ").append(seasonsWatched).append("\n");
        sb.append("Episodes unwatched: ").append(unwatched);
        return sb.toString();
    }

    /**
     * Gets the total number of episodes in all the seasons.
     *
     * @return the number of episodes
     */
    public int getNumEpisodes() {
        int count = 0;
        for (Season season : seasons) {
            count += season.getNumEpisodes();
        }
        return count;
    }

    /**
     * Recomputes the total time of the show by adding up the total time of
     * each season. Call this after any change to the episodes.
     */
    public void updateTotalTime() {
        // Start again from zero so nothing is counted twice
        totalTime = new Time("00:00:00");
        for (Season season : seasons) {
            totalTime.add(season.getTotalTime());
        }
    }

    /**
     * Gets the number of TV shows in the collection.
     *
     * @return the number of shows
     */
    public static int getNumShows() {
        return numShows;
    }

    /**
     * Lowers the count of TV shows in the collection. Call this when a show
     * is removed from the collection.
     */
    public static void showRemoved() {
        if (numShows > 0) {
            numShows--;
        }
    }

    /**
     * Adds an (unwatched) episode to a season and updates the total time.
     *
     * @param seasonNum the season to add the episode to
     * @param title     the episode title
     * @param epNum     the episode number
     * @param time      the episode length in hh:mm:ss
     * @return true if the episode was added, false if the season does not exist
     *         or the episode number is already used in that season
     */
    public boolean addEpisode(int seasonNum, String title, int epNum, String time) {
        Season season = getSeason(seasonNum);
        if (season == null) {
            return false;
        }
        boolean added = season.addEpisode(title, epNum, time);
        if (added) {
            updateTotalTime();
        }
        return added;
    }

    /**
     * Removes the episode with the given number from a season and updates the total time.
     *
     * @param seasonNum the season to remove from
     * @param epNum     the episode number to remove
     * @return true if the episode was removed, false if the season or episode does not exist
     */
    public boolean removeEpisodeByNumber(int seasonNum, int epNum) {
        Season season = getSeason(seasonNum);
        if (season == null) {
            return false;
        }
        boolean removed = season.removeByNumber(epNum);
        updateTotalTime();
        return removed;
    }

    /**
     * Removes every episode with the given title from a season (not the whole
     * show) and updates the total time.
     *
     * @param seasonNum the season to remove from
     * @param title     the episode title to remove
     * @return the number of episodes removed
     */
    public int removeEpisodesByTitle(int seasonNum, String title) {
        Season season = getSeason(seasonNum);
        if (season == null) {
            return 0;
        }
        int count = season.removeByTitle(title);
        updateTotalTime();
        return count;
    }

    /**
     * Removes all episodes whose number is in a range (inclusive) from a season
     * and updates the total time.
     *
     * @param seasonNum the season to remove from
     * @param fromEp    the first episode number of the range
     * @param toEp      the last episode number of the range
     * @return the number of episodes removed
     */
    public int removeEpisodeRange(int seasonNum, int fromEp, int toEp) {
        Season season = getSeason(seasonNum);
        if (season == null) {
            return 0;
        }
        int count = season.removeRange(fromEp, toEp);
        updateTotalTime();
        return count;
    }

    /**
     * Removes all the watched episodes from a season and updates the total time.
     *
     * @param seasonNum the season to remove from
     * @return the number of episodes removed
     */
    public int removeWatchedEpisodes(int seasonNum) {
        Season season = getSeason(seasonNum);
        if (season == null) {
            return 0;
        }
        int count = season.removeWatched();
        updateTotalTime();
        return count;
    }
}