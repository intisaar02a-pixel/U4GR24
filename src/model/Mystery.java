package model;

/**
 * A mystery tile is neutral until it is activated the first time it gets "överraskad".
 * Before activation it is shown as "?" in the UI
 *
 * @author Intisaar & Maya
 */

public class Mystery implements BoardItem{
    private final MysteryType type;
    private boolean activated;

    /**
     * Creates a new Mystery tile
     *
     * @param type mystery type
     * @author Intisaar & Maya
     */

    public Mystery (MysteryType type){
        this.type = type;
        this.activated = false;
    }

    /**
     * Returns the type
     *
     * @return type
     * @author Intisaar & Maya
     */
    public MysteryType getType(){
        return type;
    }

    /**
     * Returns whether this mystery has activated
     *
     * @return activated
     * @author Intisaar & Maya
     */

    public boolean isActivated(){
        return activated;
    }
    /**
     * Marks as activated
     *
     * @author Intisaar & Maya
     */
    public void markActivated(){
        activated = true;
    }
    /**
     * Mystery is neutral until activated (no owner):
     *
     * @return null
     * @author Intisaar & Maya
     */
    @Override
    public Player getOwner(){
        return null;
    }
    /**
     * Shows "?" until activated; after activation we still keep symbol "?" because
     * the tile itself is replaced by a piece in the board when activated
     *
     * @return symbol
     * @author Intisaar & Maya
     */
    @Override
    public String getSymbol(){
        return "?";
    }
}
