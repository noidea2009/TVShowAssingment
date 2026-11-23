/**
 * Episode.java
 * Name: Junjit Chang
 *
 * An Episode has a title, an episode number, whether it has been watched,
 * and its length (hh:mm:ss). Episodes are ordered by episode number.
 */
public class Episode implements Comparable<Episode> {
    /** The title of the episode. */
    private String title;
    /** The episode number within its season. */
    private int episodeNum;
    /** True if the episode has been watched. */
    private boolean watched;
    /** The length of the episode. */
    private Time length;

    /**
     * Creates an episode. New episodes are always unwatched.
     *
     * @param title      the episode title
     * @param episodeNum the episode number
     * @param length     the length of the episode
     */
    public Episode(String title, int episodeNum, Time length) {
        this.title = title;
        this.episodeNum = episodeNum;
        this.length = length;
        this.watched = false; // unwatched by default
    }

    /**
     * Compares this episode to another by episode number.
     *
     * @param other the episode to compare to
     * @return a negative number, 0 or a positive number if this episode's
     *         number is smaller than, equal to or larger than the other's
     */
    public int compareTo(Episode other) {
        return Integer.compare(this.episodeNum, other.episodeNum);
    }

    /**
     * Gets a one-line description of the episode.
     *
     * @return the episode number, title, length and watched status
     */
    public String toString() {
        return "Episode " + episodeNum + ": " + title + " (" + length + ", "
                + (watched ? "watched" : "unwatched") + ")";
    }

    /**
     * Gets the full information of the episode: title, season number,
     * episode number, whether it has been watched, and its length.
     *
     * @param seasonNum the number of the season the episode belongs to
     * @return the information as multi-line text
     */
    public String getInfo(int seasonNum) {
        return "Title: " + title + "\n"
                + "Season: " + seasonNum + "\n"
                + "Episode #: " + episodeNum + "\n"
                + "Watched: " + (watched ? "Yes" : "No") + "\n"
                + "Length: " + length;
    }

    /**
     * Gets the episode number.
     *
     * @return the episode number
     */
    public int getEpisodeNum() {
        return episodeNum;
    }

    /**
     * Gets the episode title.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Gets the length of the episode. A copy is returned so other classes
     * cannot change the episode's own length by accident.
     *
     * @return a copy of the length as a Time
     */
    public Time getLength() {
        return new Time(length.toString());
    }

    /**
     * Checks whether the episode has been watched.
     *
     * @return true if it has been watched
     */
    public boolean isWatched() {
        return watched;
    }

    /**
     * Sets whether the episode has been watched.
     *
     * @param watched true to mark the episode as watched
     */
    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}