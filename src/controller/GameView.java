package controller;

/**
 * Interface implemented by the GUI (view) so the controller can push updates.
 *
 * @author Intisaar & Maya
 */
public interface GameView {

    /**
     * Refreshes UI with the latest state.
     *
     * @param state current state DTO
     * @author Intisaar & Maya
     */
    void render(GameState state);

    /**
     * Shows an information message.
     *
     * @param message message
     * @author Intisaar & Maya
     */
    void showMessage(String message);

    /**
     * Prompts the user for a name (used for highscore).
     *
     * @param prompt prompt text
     * @return entered name or null
     * @author Intisaar & Maya
     */
    String askForName(String prompt);
}
