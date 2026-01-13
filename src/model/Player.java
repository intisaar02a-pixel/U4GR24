package model;

/**
 * Represents a player in the game
 *
 * @author Intisaar & Maya
 */
public enum Player {
    PLAYER1("Spelare 1", "1"),
    PLAYER2("Spelare 2", "2");

    private final String displayName;
    private final String symbol;

    Player(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    /**
     * Returns a human readable name.
     * @return display name
     * @author Intisaar & Maya
     */
    public String getDisplayName(){
        return displayName;
    }

    /**
     * Returns a short symbol for UI
     *
     * @return symbol
     * @author Intisaar & Maya
     */

    public String getSymbol(){
        return symbol;
    }

    /**
     * Returns the other player
     * @return opponent
     * @author Intisaar & Maya
     */

    public Player opponent(){
        return this == PLAYER1 ? PLAYER2 : PLAYER2;
    }
}
