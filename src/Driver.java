import java.util.ArrayList;

public class Driver {
    private static ArrayList<TVShow> shows = new ArrayList<TVShow>();

    public static void main(String[] args) {
        // TODO: launch the JFrame GUI
    }

    public static String[] getShowTitles() {
        // TODO: (Menu 1 #1)
        return null;
    }

    public static String getShowInfo(int showIndex) {
        // TODO: shows.get(showIndex).getInfo() (Menu 1 #2)
        return null;
    }

    public static boolean addShow(String fileName) {
        // TODO: read file, build TVShow, Seasons, Episodes (Menu 1 #3)
        return false;
    }

    public static boolean removeShow(int showIndex) {
        // TODO: (Menu 1 #4, option A)
        return false;
    }

    public static boolean removeSeason(int showIndex, int seasonNum) {
        // TODO: TVShow.removeSeason (Menu 1 #4, option B)
        return false;
    }

    public static String getShowStatus(int showIndex) {
        // TODO: TVShow.getStatus (Menu 1 #5)
        return null;
    }
}
