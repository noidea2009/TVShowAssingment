import java.util.ArrayList;
import java.util.Collections;

/**
 * Season.java
 * Name: Junjit Chang
 *
 * A Season has a season number, a list of Episode objects and the total
 * length of all of its episodes. Duplicate episode numbers are not allowed.
 * Whenever the episodes are in episode-number order, a binary search is used
 * to find them.
 */
public class Season implements Comparable<Season> {
    /** The season number. */
    private int seasonNum;
    /** The episodes of the season, in the last sorted order. */
    private ArrayList<Episode> episodes;
    /** The total length of all the episodes in the season. */
    private Time totalTime;
    /** True while the episodes are sorted by episode number (so binary search works). */
    private boolean sortedByNumber;

    /**
     * Creates an empty season.
     *
     * @param seasonNum the season number
     */
    public Season(int seasonNum) {
        this.seasonNum = seasonNum;
        this.episodes = new ArrayList<>();
        this.totalTime = new Time("00:00:00");
        this.sortedByNumber = true; // an empty list is already in order
    }

    /**
     * Finds the position of an episode number. Uses a binary search when the
     * episodes are sorted by number, otherwise a linear search.
     *
     * @param epNum the episode number to look for
     * @return the index of the episode if found; otherwise a negative number
     *         (-(insertion point) - 1) when sorted by number, or -1 when not
     */
    private int findIndexByNumber(int epNum) {
        if (sortedByNumber) {
            // A temporary Episode is only used as the search key (compared by number)
            return Collections.binarySearch(episodes, new Episode("", epNum, new Time("00:00:00")));
        }
        for (int i = 0; i < episodes.size(); i++) {
            if (episodes.get(i).getEpisodeNum() == epNum) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the episode titles in the last sorted order (Menu 2 #1).
     *
     * @return an array with the title of each episode
     */
    public String[] getEpisodeTitles() {
        String[] titles = new String[episodes.size()];
        for (int i = 0; i < titles.length; i++) {
            titles[i] = episodes.get(i).getTitle();
        }
        return titles;
    }

    /**
     * Gets the information of one episode (Menu 2 #2): title, season #,
     * episode #, whether it was watched and its length.
     *
     * @param epIndex the index of the episode in the current order
     * @return the information as multi-line text
     */
    public String getEpisodeInfo(int epIndex) {
        return episodes.get(epIndex).getInfo(seasonNum);
    }

    /**
     * Marks an episode as watched (Menu 2 #3).
     *
     * @param epIndex the index of the episode in the current order
     */
    public void watchEpisode(int epIndex) {
        episodes.get(epIndex).setWatched(true);
    }

    /**
     * Adds an unwatched episode (Menu 2 #4) and adds its length to the total time.
     * Duplicate episode numbers are rejected. If the episodes are sorted by
     * number the new episode is inserted in its place, otherwise it goes at the
     * end so the last sorted order is kept.
     *
     * @param title the episode title
     * @param epNum the episode number
     * @param time  the episode length as hh:mm:ss
     * @return true if the episode was added, false if the number is already used
     */
    public boolean addEpisode(String title, int epNum, String time) {
        int index = findIndexByNumber(epNum);
        if (index >= 0) {
            return false; // duplicate episode number
        }

        Episode episode = new Episode(title, epNum, new Time(time));
        if (sortedByNumber) {
            // A negative result holds the position where the episode belongs
            episodes.add(-index - 1, episode);
        } else {
            episodes.add(episode);
        }
        totalTime.add(episode.getLength());
        return true;
    }

    /**
     * Removes the episode with the given number (Menu 2 #5) and subtracts its
     * length from the total time.
     *
     * @param epNum the episode number to remove
     * @return true if the episode was removed, false if it does not exist
     */
    public boolean removeByNumber(int epNum) {
        int index = findIndexByNumber(epNum);
        if (index < 0) {
            return false;
        }
        Episode removed = episodes.remove(index);
        totalTime.subtract(removed.getLength());
        return true;
    }

    /**
     * Removes ALL the episodes of this season with the given title
     * (ignoring upper/lower case) and updates the total time.
     *
     * @param title the title to remove
     * @return the number of episodes removed
     */
    public int removeByTitle(String title) {
        int count = 0;
        // Go backwards so removing an episode does not skip the next one
        for (int i = episodes.size() - 1; i >= 0; i--) {
            if (episodes.get(i).getTitle().equalsIgnoreCase(title)) {
                episodes.remove(i);
                count++;
            }
        }
        updateTotalTime();
        return count;
    }

    /**
     * Removes all the episodes whose number is in a range (inclusive)
     * and updates the total time.
     *
     * @param fromEp the first episode number of the range
     * @param toEp   the last episode number of the range
     * @return the number of episodes removed
     */
    public int removeRange(int fromEp, int toEp) {
        int count = 0;
        for (int i = episodes.size() - 1; i >= 0; i--) {
            int num = episodes.get(i).getEpisodeNum();
            if (num >= fromEp && num <= toEp) {
                episodes.remove(i);
                count++;
            }
        }
        updateTotalTime();
        return count;
    }

    /**
     * Removes all the watched episodes and updates the total time.
     *
     * @return the number of episodes removed
     */
    public int removeWatched() {
        int count = 0;
        for (int i = episodes.size() - 1; i >= 0; i--) {
            if (episodes.get(i).isWatched()) {
                episodes.remove(i);
                count++;
            }
        }
        updateTotalTime();
        return count;
    }

    /**
     * Sorts the episodes by episode number (uses Episode's compareTo).
     */
    public void sortByNumber() {
        Collections.sort(episodes);
        sortedByNumber = true;
    }

    /**
     * Sorts the episodes by title, ignoring upper/lower case.
     */
    public void sortByTitle() {
        Collections.sort(episodes, new EpisodeTitleComparator());
        sortedByNumber = false; // binary search by number no longer works
    }

    /**
     * Sorts the episodes by length, shortest first.
     */
    public void sortByTime() {
        Collections.sort(episodes, new EpisodeTimeComparator());
        sortedByNumber = false; // binary search by number no longer works
    }

    /**
     * Recomputes the total time of the season by adding up every episode.
     */
    public void updateTotalTime() {
        // Start again from zero so nothing is counted twice
        totalTime = new Time("00:00:00");
        for (Episode episode : episodes) {
            totalTime.add(episode.getLength());
        }
    }

    /**
     * Counts the watched episodes (used by TVShow.getStatus()).
     *
     * @return the number of watched episodes
     */
    public int getWatchedCount() {
        int count = 0;
        for (Episode episode : episodes) {
            if (episode.isWatched()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Compares this season to another by season number.
     *
     * @param other the season to compare to
     * @return a negative number, 0 or a positive number if this season's
     *         number is smaller than, equal to or larger than the other's
     */
    public int compareTo(Season other) {
        return Integer.compare(this.seasonNum, other.seasonNum);
    }

    /**
     * Gets the season number.
     *
     * @return the season number
     */
    public int getSeasonNum() {
        return seasonNum;
    }

    /**
     * Gets the number of episodes in the season.
     *
     * @return the number of episodes
     */
    public int getNumEpisodes() {
        return episodes.size();
    }

    /**
     * Gets the total length of all the episodes in the season.
     *
     * @return the total time
     */
    public Time getTotalTime() {
        return totalTime;
    }

    /**
     * Gets the episode numbers in the same order as getEpisodeTitles().
     *
     * @return an array of episode numbers
     */
    public int[] getEpisodeNumbers() {
        int[] nums = new int[episodes.size()];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = episodes.get(i).getEpisodeNum();
        }
        return nums;
    }
}