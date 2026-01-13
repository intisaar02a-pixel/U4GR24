package model;

/**
 * Types of mystery tiles
 * @author Intisaar & Maya
 */

public enum MysteryType {

    TIME_JUMP, //Tidshopp: activator plays again
    NARCISSUS, // Narcissus: activator skips next
    ADDITIVE, //Additiva metoder: plus shape becomes activator pieces
    MULTIPLICITY, //Multiplicitet: diagonals + center become activator pieces
    ABYSSAL_YELL // AVGRÅNDSVRÅL: remove all sorrounding pieces
}
