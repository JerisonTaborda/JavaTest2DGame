import Players.Player;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("2D Game");
        Player player = new Player(100, 100); // Fixed: 2 arguments
        GamePanel gamePanel = new GamePanel(player);
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}