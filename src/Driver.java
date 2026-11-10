import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

/**
 * Driver.java
 * Name: (your name)
 *
 * Contains the main program for the "My TV Shows Collection" assignment.
 * It holds the ArrayList of TVShow objects, the static methods that carry out
 * the options of Menu 1 (the TV shows list), and the JFrame GUI that
 * connects Main Menu, Menu 1, Menu 2 (inside one season of one show) and the user.
 *
 * The GUI is built in the constructor: one JFrame holds one JPanel (BorderLayout)
 * with a header label (NORTH), a text output area (CENTER) and a bottom JPanel
 * (FlowLayout, SOUTH) whose buttons change depending on the menu being shown.
 *
 * NOTE: this class expects TVShow to have a getTitle() method that returns the
 * title of the show.
 */
public class Driver {
    /** The collection of all TV shows. */
    private static ArrayList<TVShow> shows = new ArrayList<TVShow>();

    // ---------- GUI components ----------

    /** The main window. */
    private JFrame myFrame;
    /** The main panel (BorderLayout) that holds everything in the window. */
    private JPanel myPanel;
    /** The bottom panel (FlowLayout) that holds the buttons of the current menu. */
    private JPanel bottomPanel;
    /** Header that names the current menu. */
    private JLabel header;
    /** Text area where all results are displayed. */
    private JTextArea output;

    // ---------- Menu 2 state ----------

    /** Index (in the collection) of the show selected for Menu 2. */
    private int currentShowIndex = -1;
    /** Season number selected for Menu 2. */
    private int currentSeasonNum = -1;
    /** Season object selected for Menu 2. */
    private Season currentSeason = null;

    //                        
    //  Constructor -- settings
    //                        

    /**
     * Builds the main window and displays the main menu.
     */
    public Driver() {
        myFrame = new JFrame("My TV Shows Collection");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        myPanel = new JPanel();
        myPanel.setLayout(new BorderLayout(10, 10));
        myPanel.setPreferredSize(new Dimension(900, 500));
        myPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header (top)
        header = new JLabel();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 20f));
        myPanel.add(header, BorderLayout.NORTH);

        // Output area (middle)
        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        output.setMargin(new Insets(8, 8, 8, 8));
        myPanel.add(new JScrollPane(output), BorderLayout.CENTER);

        // Button panel (bottom)
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        myPanel.add(bottomPanel, BorderLayout.SOUTH);

        showMainMenu();

        myFrame.add(myPanel);
        myFrame.pack();
        myFrame.setLocationRelativeTo(null);
        myFrame.setVisible(true);
    }

    /**
     * Launches the JFrame GUI.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        new Driver();
    }

    //                        
    //  Menu 1 methods (TV shows list)
    //                        

    /**
     * Gets the titles of all the TV shows in the collection (Menu 1 #1).
     *
     * @return an array with the title of each show, in collection order
     */
    public static String[] getShowTitles() {
        String[] titles = new String[shows.size()];
        for (int i = 0; i < shows.size(); i++) {
            titles[i] = shows.get(i).getTitle();
        }
        return titles;
    }

    /**
     * Gets the information (title, genre, # seasons, # episodes, total time)
     * of one TV show (Menu 1 #2).
     *
     * @param showIndex the index of the show in the collection
     * @return the show's information, or null if the index is invalid
     */
    public static String getShowInfo(int showIndex) {
        if (!isValidShowIndex(showIndex)) {
            return null;
        }
        return shows.get(showIndex).getInfo();
    }

    /**
     * Reads a TV show from a file (using a Scanner) and adds it to the collection
     * (Menu 1 #3). The file format is: title, genre, then for each season
     * "Season #", the number of episodes, and for each episode "Episode #", its
     * title and its length (hh:mm:ss). Seasons and episodes may be in any order.
     * All episodes are set to unwatched. The show is only added if the whole
     * file is read successfully.
     *
     * @param fileName the name of the input file
     * @return true if the show was read and added, false otherwise
     */
    public static boolean addShow(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        // First pass: read the whole file into temporary storage, so that a
        // bad file never leaves a half-built show in the collection
        ArrayList<String> lines = new ArrayList<String>();
        try (Scanner in = new Scanner(new File(fileName.trim()))) {
            while (in.hasNextLine()) {
                String line = in.nextLine().trim();
                if (!line.isEmpty()) { // ignore blank lines
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            return false; // file missing or unreadable
        }

        try {
            String title = lines.get(0);
            String genre = lines.get(1);

            ArrayList<Integer> seasonNums = new ArrayList<Integer>();
            // each entry: {season #, episode #, episode title, length}
            ArrayList<String[]> episodeData = new ArrayList<String[]>();

            int pos = 2;
            while (pos < lines.size()) {
                int seasonNum = parseLabelledNumber(lines.get(pos++), "Season");
                int numEpisodes = Integer.parseInt(lines.get(pos++));
                seasonNums.add(seasonNum);

                for (int e = 0; e < numEpisodes; e++) {
                    int epNum = parseLabelledNumber(lines.get(pos++), "Episode");
                    String epTitle = lines.get(pos++);
                    String length = lines.get(pos++);
                    if (!isValidTime(length)) {
                        return false;
                    }
                    episodeData.add(new String[] {String.valueOf(seasonNum),
                            String.valueOf(epNum), epTitle, length});
                }
            }

            // Second pass: the file was valid, so build the show
            TVShow show = new TVShow(title, genre);
            for (int seasonNum : seasonNums) {
                show.addSeason(seasonNum); // duplicate seasons are rejected by TVShow
            }
            for (String[] data : episodeData) {
                show.addEpisode(Integer.parseInt(data[0]), data[2],
                        Integer.parseInt(data[1]), data[3]);
            }
            // The file may not be in order, so sort each season by episode #
            for (int seasonNum : seasonNums) {
                Season season = show.getSeason(seasonNum);
                if (season != null) {
                    season.sortByNumber();
                }
            }

            shows.add(show);
            return true;
        } catch (RuntimeException e) {
            // missing lines or a number that could not be parsed
            return false;
        }
    }

    /**
     * Removes an entire TV show from the collection (Menu 1 #4, option A).
     *
     * @param showIndex the index of the show to remove
     * @return true if the show was removed, false if the index is invalid
     */
    public static boolean removeShow(int showIndex) {
        if (!isValidShowIndex(showIndex)) {
            return false;
        }
        shows.remove(showIndex);
        return true;
    }

    /**
     * Removes a season (and all of its episodes) from a TV show
     * (Menu 1 #4, option B).
     *
     * @param showIndex the index of the show
     * @param seasonNum the season number to remove
     * @return true if the season was removed, false if the show or season does not exist
     */
    public static boolean removeSeason(int showIndex, int seasonNum) {
        if (!isValidShowIndex(showIndex)) {
            return false;
        }
        return shows.get(showIndex).removeSeason(seasonNum);
    }

    /**
     * Gets the watch status of a TV show (Menu 1 #5): each season's watched
     * count, the number of fully watched seasons and the unwatched episodes.
     *
     * @param showIndex the index of the show
     * @return the status text, or null if the index is invalid
     */
    public static String getShowStatus(int showIndex) {
        if (!isValidShowIndex(showIndex)) {
            return null;
        }
        return shows.get(showIndex).getStatus();
    }

    //                        
    //  Private static helper methods
    //                        

    /**
     * Checks that an index refers to a show in the collection.
     *
     * @param showIndex the index to check
     * @return true if the index is valid
     */
    private static boolean isValidShowIndex(int showIndex) {
        return showIndex >= 0 && showIndex < shows.size();
    }

    /**
     * Checks whether a string is a valid time in the format h:mm:ss, where the
     * hours can be any number of digits, and the minutes and seconds are
     * exactly two digits each, from 00 to 59.
     *
     * @param time the string to check (may be null)
     * @return true if the string is a valid time, false otherwise
     */
    public static boolean isValidTime(String time) {
        // A null string can never be a valid time
        if (time == null) {
            return false;
        }

        // Ignore any extra spaces at the start or end
        time = time.trim();

        // Find the two colons that separate hours, minutes and seconds
        int firstColon = time.indexOf(':');
        int secondColon = time.indexOf(':', firstColon + 1);

        // Need exactly two colons: the first must exist, the second must come
        // after it, and there must not be a third one
        if (firstColon == -1 || secondColon == -1 || time.indexOf(':', secondColon + 1) != -1) {
            return false;
        }

        // Split the string into its three parts
        String hours = time.substring(0, firstColon);
        String minutes = time.substring(firstColon + 1, secondColon);
        String seconds = time.substring(secondColon + 1);

        // Hours: at least one digit, digits only
        if (hours.length() == 0 || !allDigits(hours)) {
            return false;
        }

        // Minutes and seconds: exactly two digits, and no more than 59
        return isValidSixty(minutes) && isValidSixty(seconds);
    }

    /**
     * Checks whether a string is exactly two digits long and represents a
     * number from 00 to 59 (used for minutes and seconds).
     *
     * @param part the string to check
     * @return true if the string is two digits and between 00 and 59
     */
    private static boolean isValidSixty(String part) {
        // Must be exactly two characters, all digits
        if (part.length() != 2 || !allDigits(part)) {
            return false;
        }

        // The tens digit must be 0 to 5, so the value is at most 59
        return part.charAt(0) <= '5';
    }

    /**
     * Checks whether every character in a string is a digit from 0 to 9.
     *
     * @param str the string to check
     * @return true if every character is a digit, false if any is not
     */
    private static boolean allDigits(String str) {
        // Look at each character one at a time
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Parses a line such as "Season 2" or "Episode 5" and returns the number.
     *
     * @param line   the line to parse
     * @param prefix the word that must start the line
     * @return the number after the prefix
     * @throws IllegalArgumentException if the line does not start with the prefix
     */
    private static int parseLabelledNumber(String line, String prefix) {
        if (!line.toLowerCase().startsWith(prefix.toLowerCase())) {
            throw new IllegalArgumentException("Expected " + prefix);
        }
        return Integer.parseInt(line.substring(prefix.length()).trim());
    }

    /**
     * Builds a numbered list (1, 2, 3...) from an array of titles.
     *
     * @param titles the titles to list
     * @return the list as multi-line text
     */
    private static String numberedList(String[] titles) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < titles.length; i++) {
            sb.append(i + 1).append(") ").append(titles[i]).append("\n");
        }
        return sb.toString();
    }

    //                        
    //  Switching menus (rebuilds the buttons in the bottom panel)
    //                        

    /**
     * Replaces the buttons in the bottom panel with those of a new menu.
     *
     * @param title   the header text of the menu
     * @param labels  the text of each button
     * @param actions the action run by each button (same order as labels)
     */
    private void setMenu(String title, String[] labels, Runnable[] actions) {
        header.setText(title);
        bottomPanel.removeAll(); // clear the previous menu's buttons
        for (int i = 0; i < labels.length; i++) {
            JButton button = new JButton(labels[i]);
            Runnable action = actions[i];
            button.addActionListener(e -> action.run());
            bottomPanel.add(button);
        }
        // Refresh the panel so the new buttons appear
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    /**
     * Displays the main menu.
     */
    private void showMainMenu() {
        output.setText("Welcome to My TV Shows Collection!\n\n"
                + "1) TV Shows List - view, add, remove and check the status of your shows\n"
                + "2) Within a Show - work with the episodes of one season of one show\n"
                + "3) Exit");
        setMenu("Main Menu",
                new String[] {"TV Shows List", "Within a TV Show", "Exit"},
                new Runnable[] {this::showShowsMenu, this::enterEpisodeMenu, this::exitProgram});
    }

    /**
     * Displays Menu 1 (the TV shows list).
     */
    private void showShowsMenu() {
        output.setText("Choose an option below.");
        setMenu("Your TV Shows",
                new String[] {"Display Shows", "Show Info", "Add Show", "Remove",
                        "Show Status", "Main Menu"},
                new Runnable[] {this::guiListShows, this::guiShowInfo, this::guiAddShow,
                        this::guiRemove, this::guiShowStatus, this::showMainMenu});
    }

    /**
     * Displays Menu 2 (episodes of the current show and season).
     */
    private void showEpisodeMenu() {
        setMenu(shows.get(currentShowIndex).getTitle() + " - Season " + currentSeasonNum,
                new String[] {"All Episodes", "Episode Info", "Watch", "Add", "Remove",
                        "Sort", "Main Menu"},
                new Runnable[] {this::guiListEpisodes, this::guiEpisodeInfo, this::guiWatchEpisode,
                        this::guiAddEpisode, this::guiRemoveEpisodes, this::guiSortEpisodes,
                        this::showMainMenu});
    }

    //  GUI input helpers (all include error checking)

    /**
     * Shows an error message dialog.
     *
     * @param message the message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(myFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Asks a yes/no question.
     *
     * @param message the question
     * @return true if the user answered yes
     */
    private boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(myFrame, message, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Lets the user choose one entry from a numbered list of titles.
     *
     * @param titles  the titles to choose from
     * @param prompt  the question shown to the user
     * @param heading the dialog title
     * @return the chosen index, or -1 if the user cancelled
     */
    private int chooseFromList(String[] titles, String prompt, String heading) {
        String[] options = new String[titles.length];
        for (int i = 0; i < titles.length; i++) {
            options[i] = (i + 1) + ") " + titles[i];
        }
        // Options are numbered, so duplicate titles can still be told apart
        Object choice = JOptionPane.showInputDialog(myFrame, prompt, heading,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == null) {
            return -1;
        }
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(choice)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Lets the user pick a TV show from the collection.
     *
     * @return the index of the chosen show, or -1 if none exist or the user cancelled
     */
    private int pickShow() {
        if (shows.isEmpty()) {
            showError("There are no TV shows in your collection yet.");
            return -1;
        }
        return chooseFromList(getShowTitles(), "Select a TV show:", "Select TV Show");
    }

    /**
     * Lets the user pick an episode of the current season.
     *
     * @param prompt the question shown to the user
     * @return the index of the chosen episode, or -1 if the user cancelled
     */
    private int pickEpisode(String prompt) {
        return chooseFromList(currentSeason.getEpisodeTitles(), prompt, "Select Episode");
    }

    /**
     * Repeatedly asks for a whole number until a valid one is entered.
     *
     * @param prompt the question shown to the user
     * @param min    the smallest number allowed
     * @return the number, or null if the user cancelled
     */
    private Integer readInt(String prompt, int min) {
        while (true) {
            String input = JOptionPane.showInputDialog(myFrame, prompt);
            if (input == null) {
                return null; // cancelled
            }
            try {
                int value = Integer.parseInt(input.trim());
                if (value >= min) {
                    return value;
                }
                showError("Please enter a whole number of at least " + min + ".");
            } catch (NumberFormatException e) {
                showError("Please enter a valid whole number.");
            }
        }
    }

    /**
     * Repeatedly asks for text until something non-empty is entered.
     *
     * @param prompt the question shown to the user
     * @return the trimmed text, or null if the user cancelled
     */
    private String readText(String prompt) {
        while (true) {
            String input = JOptionPane.showInputDialog(myFrame, prompt);
            if (input == null) {
                return null;
            }
            if (!input.trim().isEmpty()) {
                return input.trim();
            }
            showError("This cannot be empty.");
        }
    }

    /**
     * Repeatedly asks for a time in hh:mm:ss format until a valid one is entered.
     *
     * @param prompt the question shown to the user
     * @return the time text, or null if the user cancelled
     */
    private String readTime(String prompt) {
        while (true) {
            String input = JOptionPane.showInputDialog(myFrame, prompt);
            if (input == null) {
                return null;
            }
            if (isValidTime(input)) {
                return input.trim();
            }
            showError("Please use the format hh:mm:ss (e.g. 00:47:23).");
        }
    }

    //  GUI actions: Main menu

    /**
     * Closes the program after confirming with the user (Main Menu #3).
     */
    private void exitProgram() {
        if (confirm("Are you sure you want to exit?")) {
            myFrame.dispose();
            System.exit(0);
        }
    }

    /**
     * Main Menu #2: selects a show and season, then opens the episode menu.
     * If the season does not exist, the user may add it first.
     */
    private void enterEpisodeMenu() {
        int showIndex = pickShow();
        if (showIndex < 0) {
            return;
        }
        Integer seasonNum = readInt("Enter the season number:", 1);
        if (seasonNum == null) {
            return;
        }

        TVShow show = shows.get(showIndex);
        Season season = show.getSeason(seasonNum);

        // Offer to create a new season if it does not exist yet
        if (season == null) {
            if (!confirm("Season " + seasonNum + " does not exist. Add it as a new season?")) {
                return;
            }
            if (!show.addSeason(seasonNum)) {
                showError("Season " + seasonNum + " could not be added.");
                return;
            }
            season = show.getSeason(seasonNum);
            if (season == null) {
                showError("Season " + seasonNum + " could not be found after adding it.");
                return;
            }
        }

        currentShowIndex = showIndex;
        currentSeasonNum = seasonNum;
        currentSeason = season;

        showEpisodeMenu();
        if (hasEpisodes(false)) {
            guiListEpisodes();
        } else {
            output.setText("This season has no episodes yet.\n"
                    + "Use \"Add\" to add the first one.");
        }
    }

    //  GUI actions: Menu 1 (TV shows list)

    /**
     * Menu 1 #1: displays a numbered list of all TV show titles.
     */
    private void guiListShows() {
        if (shows.isEmpty()) {
            output.setText("There are no TV shows in your collection yet.");
            return;
        }
        output.setText("Your TV shows:\n\n" + numberedList(getShowTitles()));
    }

    /**
     * Menu 1 #2: displays the information of a chosen TV show.
     */
    private void guiShowInfo() {
        int index = pickShow();
        if (index < 0) {
            return;
        }
        output.setText(getShowInfo(index));
    }

    /**
     * Menu 1 #3: asks for a file name and adds the TV show stored in it.
     */
    private void guiAddShow() {
        String fileName = readText("Enter the name of the file to read (e.g. umbrella.txt):");
        if (fileName == null) {
            return;
        }
        if (addShow(fileName)) {
            output.setText("\"" + shows.get(shows.size() - 1).getTitle()
                    + "\" was added to your collection.");
        } else {
            showError("The file could not be read. Check the name and the file format.");
        }
    }

    /**
     * Menu 1 #4: removes either a whole TV show or one season of a show.
     */
    private void guiRemove() {
        if (shows.isEmpty()) {
            showError("There are no TV shows in your collection yet.");
            return;
        }
        String[] choices = {"Remove a TV show", "Remove a season", "Cancel"};
        int choice = JOptionPane.showOptionDialog(myFrame, "What would you like to remove?",
                "Remove", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, choices, choices[0]);
        if (choice != 0 && choice != 1) {
            return; // cancelled
        }

        int index = pickShow();
        if (index < 0) {
            return;
        }
        String title = getShowTitles()[index];

        if (choice == 0) {
            // Option A: remove the whole show
            if (confirm("Remove \"" + title + "\" from your collection?") && removeShow(index)) {
                output.setText("\"" + title + "\" was removed.");
            }
        } else {
            // Option B: remove one season (and all of its episodes)
            Integer seasonNum = readInt("Enter the season number to remove:", 1);
            if (seasonNum == null) {
                return;
            }
            if (!confirm("Remove season " + seasonNum + " and all of its episodes from \""
                    + title + "\"?")) {
                return;
            }
            if (removeSeason(index, seasonNum)) {
                output.setText("Season " + seasonNum + " of \"" + title + "\" was removed.");
            } else {
                showError("Season " + seasonNum + " does not exist in \"" + title + "\".");
            }
        }
    }

    /**
     * Menu 1 #5: displays how much of a chosen TV show has been watched.
     */
    private void guiShowStatus() {
        int index = pickShow();
        if (index < 0) {
            return;
        }
        output.setText(getShowStatus(index));
    }

    //                        
    //  GUI actions: Menu 2 (episodes of the current season)
    //                        

    /**
     * Checks that the current season has at least one episode. The other
     * options of Menu 2 do not work until an episode has been added.
     *
     * @param warn true to show an error dialog when there are no episodes
     * @return true if the season has at least one episode
     */
    private boolean hasEpisodes(boolean warn) {
        String[] titles = currentSeason.getEpisodeTitles();
        boolean has = titles != null && titles.length > 0;
        if (!has && warn) {
            showError("This season has no episodes yet. Add an episode first.");
        }
        return has;
    }

    /**
     * Menu 2 #1: displays the episode titles in the last sorted order.
     */
    private void guiListEpisodes() {
        if (!hasEpisodes(true)) {
            return;
        }
        output.setText("Episodes (last sorted order):\n\n"
                + numberedList(currentSeason.getEpisodeTitles()));
    }

    /**
     * Menu 2 #2: displays the information of a chosen episode.
     */
    private void guiEpisodeInfo() {
        if (!hasEpisodes(true)) {
            return;
        }
        int index = pickEpisode("Select an episode:");
        if (index < 0) {
            return;
        }
        output.setText(currentSeason.getEpisodeInfo(index));
    }

    /**
     * Menu 2 #3: marks a chosen episode as watched.
     */
    private void guiWatchEpisode() {
        if (!hasEpisodes(true)) {
            return;
        }
        int index = pickEpisode("Which episode did you watch?");
        if (index < 0) {
            return;
        }
        currentSeason.watchEpisode(index);
        output.setText("Episode marked as watched.\n\n" + currentSeason.getEpisodeInfo(index));
    }

    /**
     * Menu 2 #4: asks for a title, episode number and length and adds the
     * (unwatched) episode to the current season.
     */
    private void guiAddEpisode() {
        String title = readText("Enter the episode title:");
        if (title == null) {
            return;
        }
        Integer epNum = readInt("Enter the episode number:", 1);
        if (epNum == null) {
            return;
        }
        String time = readTime("Enter the episode length (hh:mm:ss):");
        if (time == null) {
            return;
        }

        // TVShow adds the episode and updates the total times
        if (shows.get(currentShowIndex).addEpisode(currentSeasonNum, title, epNum, time)) {
            output.setText("Episode " + epNum + " was added.\n\n"
                    + numberedList(currentSeason.getEpisodeTitles()));
        } else {
            showError("Episode number " + epNum + " already exists in this season.");
        }
    }

    /**
     * Menu 2 #5: removes episodes by number, by title, by a range, or only
     * the watched ones.
     */
    private void guiRemoveEpisodes() {
        if (!hasEpisodes(true)) {
            return;
        }
        String[] choices = {"By episode #", "By title", "By range", "Watched only", "Cancel"};
        int choice = JOptionPane.showOptionDialog(myFrame, "How would you like to remove episodes?",
                "Remove Episodes", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, choices, choices[0]);
        if (choice < 0 || choice > 3) {
            return; // cancelled
        }

        TVShow show = shows.get(currentShowIndex);
        String result;

        if (choice == 0) {
            // Remove a single episode by its number
            Integer epNum = readInt("Enter the episode number to remove:", 1);
            if (epNum == null) {
                return;
            }
            result = show.removeEpisodeByNumber(currentSeasonNum, epNum)
                    ? "Episode " + epNum + " was removed."
                    : "Episode " + epNum + " does not exist in this season.";
        } else if (choice == 1) {
            // Remove every episode with the given title
            String title = readText("Enter the episode title to remove:");
            if (title == null) {
                return;
            }
            int count = show.removeEpisodesByTitle(currentSeasonNum, title);
            result = count + " episode(s) titled \"" + title + "\" removed.";
        } else if (choice == 2) {
            // Remove all episodes whose number is within a range
            Integer from = readInt("Remove from episode #:", 1);
            if (from == null) {
                return;
            }
            Integer to = readInt("Remove up to and including episode #:", from);
            if (to == null) {
                return;
            }
            int count = show.removeEpisodeRange(currentSeasonNum, from, to);
            result = count + " episode(s) removed (episodes " + from + " to " + to + ").";
        } else {
            // Remove only the episodes that were already watched
            if (!confirm("Remove all watched episodes from this season?")) {
                return;
            }
            int count = show.removeWatchedEpisodes(currentSeasonNum);
            result = count + " watched episode(s) removed.";
        }

        // Re-display whatever is left in the season
        if (hasEpisodes(false)) {
            result += "\n\n" + numberedList(currentSeason.getEpisodeTitles());
        } else {
            result += "\n\nThis season now has no episodes.";
        }
        output.setText(result);
    }

    /**
     * Menu 2 #6: sorts the episodes by number, title or time, then
     * re-displays the list.
     */
    private void guiSortEpisodes() {
        if (!hasEpisodes(true)) {
            return;
        }
        String[] choices = {"Episode #", "Title", "Time", "Cancel"};
        int choice = JOptionPane.showOptionDialog(myFrame, "Sort the episodes by:",
                "Sort Episodes", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, choices, choices[0]);

        String sortedBy;
        if (choice == 0) {
            currentSeason.sortByNumber();
            sortedBy = "episode #";
        } else if (choice == 1) {
            currentSeason.sortByTitle();
            sortedBy = "title";
        } else if (choice == 2) {
            currentSeason.sortByTime();
            sortedBy = "time";
        } else {
            return; // cancelled
        }
        output.setText("Episodes sorted by " + sortedBy + ":\n\n"
                + numberedList(currentSeason.getEpisodeTitles()));
    }
}