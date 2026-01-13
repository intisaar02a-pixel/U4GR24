package controller;
import model.*;
import java.util.List;

/**
 * Control class connecting view events to model logic.
 *
 * @author Intisaar & Maya
 */
public class GameControllerImpl implements GameController {

    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final int MYSTERIES = 5;

    private final Game model;
    private final HighscoreManager highscoreManager;
    private final GameView view;

    /**
     * Creates the controller.
     *
     * @param view view callback interface
     * @author Intisaar & Maya
     */
    public GameControllerImpl(GameView view) {
        this.view = view;
        this.model = new Game();
        this.highscoreManager = new HighscoreManager("highscores.txt");
    }

    /**
     * Starts a new game and renders.
     *
     * @author Intisaar & Maya
     */
    @Override
    public void newGame() {
        model.newGame(ROWS, COLS, MYSTERIES);
        pushStateToView();
    }

    /**
     * Handles a click from view.
     *
     * @param row row
     * @param col col
     * @author Intisaar & Maya
     */
    @Override
    public void onCellClicked(int row, int col) {
        if (model.isGameOver()) {
            view.showMessage("Spelet är redan slut. Starta ett nytt spel.");
            return;
        }

        MoveResult result = model.placePiece(row, col);
        if (!result.valid()) {
            view.showMessage(result.message());
            pushStateToView();
            return;
        }

        // Advance turn based on move/mystery effects
        model.advanceTurn(result.extraTurn(), result.skipNextTurnForCurrent());

        // Check endgame and handle highscore
        if (model.isGameOver()) {
            handleGameOver();
        }

        pushStateToView();
    }

    /**
     * Returns highscores as formatted text.
     *
     * @return formatted text
     * @author Intisaar & Maya
     */
    @Override
    public String getHighscoreText() {
        StringBuilder sb = new StringBuilder();
        List<HighscoreEntry> top = highscoreManager.getTop10();
        sb.append("TOP 10 HIGHSCORE\n");
        sb.append("-----------------\n");
        int i = 1;
        for (HighscoreEntry e : top) {
            sb.append(i).append(". ").append(e.name()).append(" - ").append(e.score()).append("\n");
            i++;
        }
        if (top.isEmpty()) {
            sb.append("(Tom lista)\n");
        }
        return sb.toString();
    }

    /**
     * Converts model board to view state and calls render().
     *
     * @author Intisaar & Maya
     */
    private void pushStateToView() {
        Board b = model.getBoard();
        CellView[][] cells = new CellView[b.getRows()][b.getCols()];

        for (int r = 0; r < b.getRows(); r++) {
            for (int c = 0; c < b.getCols(); c++) {
                BoardItem item = b.get(r, c);
                String text;
                if (item == null) text = "";
                else text = item.getSymbol();

                boolean enabled = (item == null) && !model.isGameOver();
                cells[r][c] = new CellView(text, enabled);
            }
        }

        GameState state = new GameState(
                cells,
                model.getCurrentPlayer(),
                model.getScore(Player.PLAYER1),
                model.getScore(Player.PLAYER2),
                b.countInactiveMysteries(),
                model.isGameOver()
        );

        view.render(state);
    }

    /**
     * Handles endgame UI + optional highscore input.
     *
     * @author Intisaar & Maya
     */
    private void handleGameOver() {
        Player winner = model.getWinnerOrNullIfTie();
        int p1 = model.getScore(Player.PLAYER1);
        int p2 = model.getScore(Player.PLAYER2);

        String message;
        if (winner == null) {
            message = "Spelet är slut! Oavgjort.\nSpelare 1: " + p1 + " poäng, Spelare 2: " + p2 + " poäng.";
            view.showMessage(message);
            return;
        }

        int winnerScore = model.getScore(winner);
        message = "Spelet är slut! Vinnare: " + winner.getDisplayName() + " med " + winnerScore + " poäng.";
        view.showMessage(message);

        if (highscoreManager.qualifies(winnerScore)) {
            String name = view.askForName("Du kvalade in på highscore! Ange namn:");
            if (name != null && !name.trim().isEmpty()) {
                highscoreManager.addEntry(name.trim(), winnerScore);
                view.showMessage("Highscore uppdaterad!\n\n" + getHighscoreText());
            }
        } else {
            view.showMessage(getHighscoreText());
        }
    }
}
