import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame jframe = new JFrame("Flappy Bird");
        // jframe.setVisible(true);
        jframe.setSize(boardWidth, boardHeight);
        jframe.setResizable(false);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setLocationRelativeTo(null);


        FlappyBird flappyBird = new FlappyBird();
        jframe.add(flappyBird);
        jframe.pack();
        
        flappyBird.requestFocus();
        jframe.setVisible(true);
    }
}
