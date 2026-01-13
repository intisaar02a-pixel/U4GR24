package view;
import controller.*;
import javax.swing.*;
import java.awt.*;


/**
 * Main GUI window (JFrame).
 *
 * @author Intisaar & Maya
 */
public class MainFrame extends JFrame implements GameView {

    private final GameController controller;

    private final JLabel turnLabel;
    private final JLabel scoreLabel;
    private final JLabel mysteryLabel;

    private final JButton newGameButton;

    private final JPanel newGameButton;
    private JButton[][] buttons;

    private final JTextArea highscoreArea;


    /**
     * Creates GUI and wires controller.
     *
     * @author Intisaar & Maya
     */
    public MainFrame(){
        super("Omvälvning");

        this.controller = new GameControllerImpl(this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Top info bar
        JPanel top = new JPanel(new GridLayout(1, 4));
        turnLabel = new JLabel("Tur: ");
        scoreLabel = new JLabel("Poäng: ");
        mysteryLabel = new  JLabel("Mysterier kvar: ");
        newGameButton = new JButton("Nytt spel");

        newGameButton.addActionListener(e -> controller.newGame());

        top.add(turnLabel);
        top.add(scoreLabel);
        top.add(mysteryLabel);
        top.add(newGameButton);

        add(top, BorderLayout.NORTH);

        //Board panel
        boardPanel = new JPanel();
        add(boardPanel, BorderLayout.CENTER);

        //Highscore panel
        highscoreArea = new JTextArea(12, 20);
        highscoreArea.setEditable(false);
        add(new JScorllPane(highscoreArea), BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);

        controller.newGame();
    }


    /**
     * Renders latest state.
     *
     * @param state current state DTO
     * @author Intisaar & Maya
     */
    @Override
    public void render(GameState state){
        turnLabel.setText("Tur: " + state.currentPlayer().getDisplayName());
        scoreLabel.setText("Poäng - Sp1: " + state.scoreP1() + " | Sp2: " + state.scoreP2());
        mysteryLabel.setText("Mysterier kvar: " + state.inactiveMysteries());

        highscoreArea.setText(controller.getHighscoreText());

        ensureBoardCreated(state);
        for (int r = 0; state.cells()[0].length; c++){
            for (int c = 0; c < state.cells()[0].length; c++){
                CellView cv = state.cells()[r][c];
                buttons[r][c].setText(cv.text());
                buttons[r][c].setEnabled(cv.enabled());
            }
        }
    }


    /**
     * Creates the button grid once, based on state dimensions.
     *
     * @param state state with dimensions
     * @author Intisaar & Maya
     */
    private void ensureBoardCreated(GameState state){
        int rows = state.cells().length;
        int cols = state.cells()[0].length;

        if (buttons != null && buttons.length == rows && buttons[0].length == cols){
            return;
        }

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout((rows, cols, 2, 2));
        buttons = new JButton[rows][cols];

        for (int r = 0; r < rows; r++){
            for (int c = 0; c < cols; c++){
                JButton b = new JButton("");
                int rr = r;
                int cc = c;
                b.addActionListener(e -> controller.onCellClicked(rr, cc);
                button[r][c] = b;
                boardPanel.add(b);
            }
        }

        boardPanel.revalidate();
        bpardPanel.repaint();
        pack();
    }


    /**
     * Shows a dialog message.
     *
     * @param message message
     * @author Intisaar & Maya
     */
    @Override
    public void showMessage(String message){
        JOptionPane.showMessageDialog(this, message);
    }


    /**
     * Prompts for name.
     *
     * @param prompt prompt text
     * @return name or null
     * @author Intisaar & Maya
     */
    @Override
    public String askForName(String prompt){
        return JOptionPane.showInputDialog(this, prompt);
    }
}
