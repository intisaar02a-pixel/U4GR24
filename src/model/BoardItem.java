package model;

/**
 * Marker interface for anything that can be stored in the board array.
 * The board uses a BoardItem[][] array, where null means empty
 * @author Intisaar & Maya
 */
public interface BoardItem {

    /**
     * Rteurns the owner of this item if it is owned, otherwise null.
     * (Mystery is neautral until activated.)
     *
     * @return owner or null
     * @author Intisaar & Maya
     */

    Player getOwner();

    /**
     * Returns a UI symbol for this item
     *
     * @return symbol string
     * @author Intisaar & Maya
     */

    String getSymbol();
}
