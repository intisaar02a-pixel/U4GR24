package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core game logic (model/entity). No Swing imports.
 *
 * @author Intisaar & Maya
 */
public class Game {

    private final Random rng;
    private Board board;

    private Player currentPlayer;
    private boolean player1HasPlacedFirst;
    private boolean player2HasPlacedFirst;

    private int skipTurnsPlayer1;
    private int skipTurnsPlayer2;

    private int initialMysteryCount;

    /**
     * Creates a game with a random seed.
     *
     * @author Intisaar & Maya
     */
    public Game() {
        this(new Random());
    }

    /**
     * Creates a game with injected Random (useful for testing).
     *
     * @param rng random generator
     * @author Intisaar & Maya
     */
    public Game(Random rng) {
        this.rng = rng;
    }

    /**
     * Starts a new game (creates new board and places mysteries).
     *
     * @param rows rows
     * @param cols cols
     * @param mysteryCount number of mysteries (>=5 for assignment)
     * @author Intisaar & Maya
     */
    public void newGame(int rows, int cols, int mysteryCount) {
        this.board = new Board(rows, cols);
        this.board.placeRandomMysteries(mysteryCount, rng);
        this.initialMysteryCount = mysteryCount;

        this.currentPlayer = Player.PLAYER1;
        this.player1HasPlacedFirst = false;
        this.player2HasPlacedFirst = false;
        this.skipTurnsPlayer1 = 0;
        this.skipTurnsPlayer2 = 0;
    }

    /**
     * Returns current board.
     *
     * @return board
     * @author Intisaar & Maya
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns current player.
     *
     * @return current player
     * @author Intisaar & Maya
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Attempts to place a piece at (r,c).
     *
     * Rules implemented:
     * 1.First piece for each player can be placed anywhere empty.
     * 2.Later placements must be on empty cell adjacent (8-neigh) to any existing item.
     * 3."Överraskning" works in all 8 directions like Othello.
     * 4.Mystery can be "överraskad" and activates only once.
     *
     * @param r row
     * @param c col
     * @return move result
     * @author Intisaar & Maya
     */
    public MoveResult placePiece(int r, int c) {
        if (!board.inBounds(r, c)) {
            return new MoveResult(false, "Utanför spelplanen.", List.of(), false, false);
        }
        if (!board.isEmpty(r, c)) {
            return new MoveResult(false, "Platsen är inte ledig.", List.of(), false, false);
        }

        if (!isFirstPlacementAllowed(currentPlayer) && !board.isAdjacentToAnyItem(r, c)) {
            return new MoveResult(false, "Du måste placera bredvid en befintlig pjäs.", List.of(), false, false);
        }

        // Place the new piece first
        board.set(r, c, new NormalPiece(currentPlayer));
        markFirstPlacement(currentPlayer);

        // Then do surprise conversions in 8 directions
        List<Position> activatedMysteries = new ArrayList<>();
        boolean extraTurn = false;
        boolean skipNext = false;

        int[] dirs = {-1, 0, 1};
        for (int dr : dirs) {
            for (int dc : dirs) {
                if (dr == 0 && dc == 0) continue;
                SurpriseOutcome outcome = applySurpriseDirection(r, c, dr, dc);
                activatedMysteries.addAll(outcome.activatedMysteries());
                extraTurn = extraTurn || outcome.extraTurn();
                skipNext = skipNext || outcome.skipNextTurnForCurrent();
            }
        }

        // If the placed position was adjacent and created no conversions, that's okay (assignment does not require conversion).
        // Endgame checks done by controller.

        return new MoveResult(true, "Drag genomfört.", activatedMysteries, extraTurn, skipNext);
    }

    /**
     * Helper record for one-direction surprise.
     *
     * @param activatedMysteries activated mystery positions
     * @param extraTurn extra turn for current player
     * @param skipNextTurnForCurrent current player should skip next turn
     * @author Intisaar & Maya
     */
    private record SurpriseOutcome(List<Position> activatedMysteries, boolean extraTurn, boolean skipNextTurnForCurrent) { }

    /**
     * Applies surprise in a single direction.
     *
     * We collect a chain of opponent pieces and/or inactive mysteries.
     * If we later reach a current player's piece, the chain is flipped/activated.
     *
     * @param startR start row (newly placed piece)
     * @param startC start col
     * @param dr row delta
     * @param dc col delta
     * @return outcome of this direction
     * @author Intisaar & Maya
     */
    private SurpriseOutcome applySurpriseDirection(int startR, int startC, int dr, int dc) {
        List<Position> chain = new ArrayList<>();
        int r = startR + dr;
        int c = startC + dc;

        // Collect contiguous "flippable" items
        while (board.inBounds(r, c)) {
            BoardItem item = board.get(r, c);
            if (item == null) {
                return new SurpriseOutcome(List.of(), false, false);
            }

            if (item instanceof Piece p) {
                if (p.getOwner() == currentPlayer) {
                    // We found closing piece. Flip the chain.
                    if (chain.isEmpty()) return new SurpriseOutcome(List.of(), false, false);
                    return flipChain(chain);
                } else {
                    chain.add(new Position(r, c));
                }
            } else if (item instanceof Mystery m) {
                // Mystery can be part of chain if not activated yet
                if (!m.isActivated()) {
                    chain.add(new Position(r, c));
                } else {
                    // Activated mysteries do not exist as Mystery in board (we replace them),
                    // but if they do, treat as blocker.
                    return new SurpriseOutcome(List.of(), false, false);
                }
            } else {
                return new SurpriseOutcome(List.of(), false, false);
            }

            r += dr;
            c += dc;
        }

        return new SurpriseOutcome(List.of(), false, false);
    }

    /**
     * Flips a chain of positions (opponent pieces become current player's pieces),
     * and mysteries activate once.
     *
     * @param chain positions to process
     * @return outcome
     * @author Intisaar & Maya
     */
    private SurpriseOutcome flipChain(List<Position> chain) {
        List<Position> activated = new ArrayList<>();
        boolean extraTurn = false;
        boolean skipNext = false;

        for (Position pos : chain) {
            BoardItem item = board.get(pos.row(), pos.col());
            if (item instanceof Piece p) {
                p.setOwner(currentPlayer);
            } else if (item instanceof Mystery m && !m.isActivated()) {
                // Activate and replace tile with a piece (unless effect removes it, etc.)
                m.markActivated();
                activated.add(pos);

                MysteryResult result = activateMysteryEffect(m.getType(), pos, currentPlayer);
                extraTurn = extraTurn || result.extraTurn();
                skipNext = skipNext || result.skipNextTurnForActivator();
            }
        }

        return new SurpriseOutcome(activated, extraTurn, skipNext);
    }

    /**
     * Executes the effect of a Mystery and returns turn-modifier info.
     *
     * Implemented mysteries (>=3 required):
     * 1.Tidshopp
     * 2.Narcissus
     * 3.Additiva metoder
     * 4.Multiplicitet
     * 5.Avgrundsvrål
     *
     * Note: Effects explicitly do NOT trigger additional surprises (assignment requirement),
     * so we only place/remove pieces directly.
     *
     * @param type mystery type
     * @param center center position
     * @param activator activating player
     * @return result about turn changes
     * @author Intisaar & Maya
     */
    private MysteryResult activateMysteryEffect(MysteryType type, Position center, Player activator) {
        // Replace the mystery tile itself with activator piece in most cases
        board.set(center.row(), center.col(), new NormalPiece(activator));

        switch (type) {
            case TIME_JUMP -> {
                return new MysteryResult(true, false);
            }
            case NARCISSUS -> {
                return new MysteryResult(false, true);
            }
            case ADDITIVE -> {
                // Plus shape: center already set, then N,S,E,W
                placeIfEmptyOrConvert(center.row() - 1, center.col(), activator);
                placeIfEmptyOrConvert(center.row() + 1, center.col(), activator);
                placeIfEmptyOrConvert(center.row(), center.col() - 1, activator);
                placeIfEmptyOrConvert(center.row(), center.col() + 1, activator);
                return new MysteryResult(false, false);
            }
            case MULTIPLICITY -> {
                // Diagonals + center
                placeIfEmptyOrConvert(center.row() - 1, center.col() - 1, activator);
                placeIfEmptyOrConvert(center.row() - 1, center.col() + 1, activator);
                placeIfEmptyOrConvert(center.row() + 1, center.col() - 1, activator);
                placeIfEmptyOrConvert(center.row() + 1, center.col() + 1, activator);
                return new MysteryResult(false, false);
            }
            case ABYSSAL_YELL -> {
                // Remove all surrounding pieces (and mysteries) around the center.
                for (Position n : board.neighbors8(center)) {
                    board.set(n.row(), n.col(), null);
                }
                return new MysteryResult(false, false);
            }
            default -> {
                return new MysteryResult(false, false);
            }
        }
    }

    /**
     * Places a piece for activator. If there is an opponent piece, convert it.
     * If there is a Mystery, do NOT activate it here (effects should not cause surprises).
     *
     * @param r row
     * @param c col
     * @param player player
     * @author Intisaar & Maya
     */
    private void placeIfEmptyOrConvert(int r, int c, Player player) {
        if (!board.inBounds(r, c)) return;
        BoardItem item = board.get(r, c);
        if (item == null) {
            board.set(r, c, new NormalPiece(player));
        } else if (item instanceof Piece p) {
            p.setOwner(player);
        } // Mystery stays untouched during effect
    }

    /**
     * Advances to next turn considering extra-turn/skip rules.
     *
     * @param extraTurn if true, current player plays again
     * @param skipNextForCurrent if true, current player skips their next turn (stored)
     * @author Intisaar & Maya
     */
    public void advanceTurn(boolean extraTurn, boolean skipNextForCurrent) {
        if (skipNextForCurrent) {
            if (currentPlayer == Player.PLAYER1) skipTurnsPlayer1++;
            else skipTurnsPlayer2++;
        }

        if (extraTurn) {
            // Same player continues, but if they owe a skip, it should apply when they would play next time.
            return;
        }

        currentPlayer = currentPlayer.opponent();

        // Apply skip if needed
        if (currentPlayer == Player.PLAYER1 && skipTurnsPlayer1 > 0) {
            skipTurnsPlayer1--;
            currentPlayer = currentPlayer.opponent();
        } else if (currentPlayer == Player.PLAYER2 && skipTurnsPlayer2 > 0) {
            skipTurnsPlayer2--;
            currentPlayer = currentPlayer.opponent();
        }
    }

    /**
     * Returns whether game is over:
     * - no empty places OR
     * - all mysteries activated (i.e., no inactive mysteries remain)
     *
     * @return true if game over
     * @author Intisaar & Maya
     */
    public boolean isGameOver() {
        if (board.countEmpty() == 0) return true;
        return board.countInactiveMysteries() == 0;
    }

    /**
     * Returns initial mystery count for UI info.
     *
     * @return initial count
     * @author Intisaar & Maya
     */
    public int getInitialMysteryCount() {
        return initialMysteryCount;
    }

    /**
     * Returns score (number of pieces) for a player.
     *
     * @param player player
     * @return score
     * @author Intisaar & Maya
     */
    public int getScore(Player player) {
        return board.countPieces(player);
    }

    /**
     * Returns the winner (null if tie).
     *
     * @return winner or null
     * @author Intisaar & Maya
     */
    public Player getWinnerOrNullIfTie() {
        int p1 = getScore(Player.PLAYER1);
        int p2 = getScore(Player.PLAYER2);
        if (p1 > p2) return Player.PLAYER1;
        if (p2 > p1) return Player.PLAYER2;
        return null;
    }

    /**
     * Checks if current player's first placement is allowed anywhere.
     *
     * @param player player
     * @return true if first piece not yet placed
     * @author Intisaar & Maya
     */
    private boolean isFirstPlacementAllowed(Player player) {
        return (player == Player.PLAYER1 && !player1HasPlacedFirst)
                || (player == Player.PLAYER2 && !player2HasPlacedFirst);
    }

    /**
     * Marks that the player has placed their first piece.
     *
     * @param player player
     * @author Intisaar & Maya
     */
    private void markFirstPlacement(Player player) {
        if (player == Player.PLAYER1) player1HasPlacedFirst = true;
        else player2HasPlacedFirst = true;
    }
}
