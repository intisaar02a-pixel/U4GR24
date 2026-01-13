package view;
import javax.swing.*;


/**
 * Application entry point.
 *
 * @author Intisaar & Maya
 */
public class App {

    /**
     * Starts Swing GUI on EDT.
     *
     * @param args args
     * @author Intisaar & Maya
     */
    public static void main(String[] args){
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
