package model;

/**
 * Result information after a Mystery activates,
 *
 * extraTurn = true means the activator should play one more time immediately
 * skipNextTurnForActivator = true means the activator loses their next turn
 *
 * @param extraTurn extra turn granted
 * @param skipNextTurnForActivator activator loses next turn
 * @author Intisaar & Maya
 */

public record MysteryResult(boolean extraTurn, boolean skipNextTurnActivator) {
}
