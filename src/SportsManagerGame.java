import game.gui.SportsManagerFrame;

import javax.swing.SwingUtilities;

public class SportsManagerGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SportsManagerFrame().setVisible(true));
    }
}
