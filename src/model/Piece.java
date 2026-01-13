package model;

/**
 * Abstract base class for a piece on the board .
 * @author Intisaar & Maya
 */

public abstract class Piece implements BoardItem {
    private Player owner;

    /**
     * Creates a piece owned by a player
     * @param owner owner player
     * @author Intisaar & Maya
     */

    protected Piece(Player owner){
        this.owner = owner;
    }

    /**
     * Returns current owner
     *
     * @return owner
     * @author Intisaar & Maya
     */
    @Override
    public Player getOwner(){
        return owner;
    }

    /**
     * Rteurns piece symbol.
     *
     * @return symbol
     * @author Intisaar & Maya
     */
    @Override
    public String getSymbol(){
        return owner.getSymbol();
    }

}
