package model;

import javax.swing.text.Position;
import java.util.List;

/**
 * Result of a move (placing a piece).
 *
 * @param valid whether the move was valid
 * @param message optional message for user
 * @param activatedMysteries list of positions where mysteries activated
 * @param extraTurn whether current player gets another turn
 * @param skipNextTurnForCurrent whether current player should skip their next turn
 * @author Intisaar & Maya
 */
public record MoveResult(
        boolean valid,
        String message,
        List<Position> activatedMysteries,
        boolean extraTurn,
        boolean skipNextTurnForCurrent
){

}
