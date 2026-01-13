package controller;
import model.Player;


/**
 * DTO containing everything the view needs.
 *
 * @param cells 2D array of cell views
 * @param currentPlayer current player
 * @param scoreP1 score player1
 * @param scoreP2 score player2
 * @param inactiveMysteries how many mysteries remain inactive
 * @param gameOver whether game is over
 * @author Intisaar & Maya
 */
public record GameState(
        CellView[][] cells,
        Player currentPlayer,
        int scoreP1,
        int scoreP2,
        int inactiveMysteries,
        boolean gameOver
){

}
