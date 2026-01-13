package controller;


/**
 * Controller API used by the view.
 *
 * @author Intisaar & Maya
 */
public interface GameController {

    /**
     * Starts or restarts a new game.
     *
     * @author Intisaar & Maya
     */
    void newGame();

    /**
     * Called by view when a cell is clicked.
     *
     * @param row row
     * @param col col
     * @author Intisaar & Maya
     */
    void onCellClicked(int row, int col);

    /**
     * Returns highscores formatted as text for UI.
     *
     * @return highscores
     * @author Intisaar & Maya
     */
    String getHighscoreText();
}

