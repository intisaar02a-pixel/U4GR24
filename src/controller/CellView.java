package controller;

/**
 * Simple cell representation for the view (no model imports needed except enums if you want).
 *
 * @param text text to display
 * @param enabled whether button should be clickable
 * @author Intisaar & Maya
 */
public record CellView(String text, boolean enabled) {
}
