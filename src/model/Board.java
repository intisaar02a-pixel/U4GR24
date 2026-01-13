package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Boards holds the BoardItem[][] array and provides utility methods
 *
 * @author Intisaar & Maya
 */

public class Board {
    private final int rows;
    private final int cols;
    private final BoardItem[][] grid;

    /**
     * Creates an empty board
     *
     * @param rows number of rows
     * @param cols number of cols
     * @author Intisaar & Maya
     */

    public Board(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.grid = new BoardItem[rows][cols];
    }
    /**
     * Return number of rows
     *
     * @return rows
     * @author Intisaar & Maya
     *
     */
    public int getRows(){
        return rows;
    }
    /**
     * Returns number of columns
     *
     * @return cols
     * @author Intisaar & Maya
     */
    public int getCols(){
        return cols;
    }
    /**
     * Returns item at position (may be null)
     *
     * @param r row
     * @param c col
     * @return item or null
     * @author Intisaar & Maya
     */
    public BoardItem get(int r, int c){
        if (!inBounds(r, c)) return null;
        return grid[r][c];
    }
    /**
     * Sets a board item (may set null for empty).
     *
     * @param r row
     * @param c col
     * @param item item or null
     * @author Intisaar & Maya
     */

    public void set(int r, int c,BoardItem item){
        if(!inBounds(r,c)) return;
        grid[r][c] = item;
    }

    /**
     * Checks bounds
     *
     * @param r row
     * @param c col
     * @return true if within bounds
     * @author Intisaar & Maya
     */

    public boolean inBounds(int r, int c){
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }
    /**
     * Returns true if cell is empty
     *
     * @param r row
     * @param c col
     * @return empty
     *  @author Intisaar & Maya
     */
    public boolean isEmpty(int r, int c){
        return inBounds(r, c) && grid[r][c] == null;

    }

    /**
     * Counts empty cells
     *
     * @return empty count
     * @author Intisaar & Maya
     */
    public int countEmpty(){
        int count = 0;
        for (int r = 0; r < rows; r++){
            for (int c = 0; c < cols; c++){
                if (grid[r][c] == null) count++;
            }
        }
        return count;
    }

    /**
     * Counts mysteries that are still present and not activated yet
     *
     * @return count
     * @author Intisaar & Maya
     */
    public int countInactiveMysteries(){
        int count = 0;
        for (int r = 0; r < rows; r++){
            for (int c = 0; c < cols; c++){
                if (grid[r][c] instanceof Mystery m && !m.isActivated()){
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Counts piecces owned by a player
     *
     * @param player player
     * @return number of pieces
     * @author Intisaar & Maya
     */
    public int countPieces(Player player){
        int count = 0;
        for (int r = 0; r < rows; r++){
            for (int c = 0; c < cols; c++){
                BoardItem item = grid[r][c];
                if (item instanceof Piece p && p.getOwner() == player){
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns true if there is any neighboring (8-direction) non-empty cell.
     *
     * @param r row
     * @param c col
     * @reutnr true if adjacent to something
     * @author Intisaar & Maya
     */

    public boolean isAdjacentToAnyItem(int r, int c){
        for (int dr = -1; dr <= 1; dr++){
            for (int dc = -1; dc <= 1; dc++){
                if (dr == 0 && dc == 0 ) continue;
                int nr = r + dr;
                int nc = c + dc;
                if (inBounds(nr, nc) && grid[nr][nc] != null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Places mysteries randomly with constraints:
     * -Not in corners
     * -Can be on edges
     * -Must not touch each other (also diagonals)
     *
     * @param count number of mysteries
     * @param rng random generator
     * @author Intisaar & Maya
     */

    public void placeRandomMysteries(int count, Random rng){
        int placed = 0;
        while (placed < count){
            int r = rng.nextInt(rows);
            int c = rng.nextInt(cols);

            if (!isEmpty(r, c)) continue;
            if (isCorner(r, c)) continue;
            if (touchesAnyMystery(r, c)) continue;

            MysteryType type = MysteryType.values()[rng.nextInt(MysteryType.values().length)];
            set(r, c, new Mystery(type));
            placed++;
        }
    }
    /**
     * Returns whether (r,c) is a corner
     *
     * @param r row
     * @param c col
     * @return true if corner
     * @author Intisaar & Maya
     */

    public boolean isCorner(int r, int c){
        return (r == 0 && c == 0) || (r == 0 && c == cols -1) || (r == rows -1 && c == 0) || (r == rows -1 && c == cols -1);
    }
    /**
     * Returns true if any neighboring cell (8-direction) is a mystery.
     *
     * @param r row
     * @param c col
     * @return touches a mystery
     * @author Intisaar & Maya
     */
    private boolean touchesAnyMystery(int r, int c){
        for (int dr = -1; dr <= 1; dr++){
            for (int dc = -1; dc <= 1; dc++){
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr;
                int nc = c + dc;
                if (inBounds(nr, nc) && grid[nr][nc] instanceof Mystery){
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Returns positions of neighbors (8-direction) around a center cell (only in bounds).
     *
     * @param center center position
     * @return list of neighbor positions
     * @author Intisaar & Maya
     */
    public List<Position> neighbors8(Position center){
        List<Position> result = new ArrayList<>();
        for( int dr = -1; dr <= 1; dr++){
            for(int dc = -1; dc <= 1; dc++){
                if (dr == 0 && dc == 0) continue;
                int nr = center.row() + dr;
                int nc = center.col() + dc;
                if(inBounds(nr, nc)) result.add(new Position(nr, nc));

            }
        }
        return result;
    }
}
