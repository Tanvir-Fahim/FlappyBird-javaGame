import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image backgroundImg;

    // Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // scaled by 1/6.
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    // Game logic
    Bird bird;

    int velocityX = -4; // moving the pipes to the left speed (siulates the bird moving right)
    int velocityY = 0; // move bird up/down speed.
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // bird:
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // place pipe timer.
        placePipesTimer = new Timer(1500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });

        placePipesTimer.start();

        // game timer
        gameLoop = new Timer(1000/60, this); // 1000/60 = 16.6
        gameLoop.start();
    }

    public void placePipes(){
        // (0-1) * pipeHeight/2 --> (0-256)
        // 128
        // 0 - 128 - (0-256) --> [1/4 pipeHeight -- 3/4 pipeHeight ]
        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = pipeHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        // Background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score
        g.setColor(Color.yellow);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver){
            g.setColor(Color.RED);
            Font font = new Font("Arial", Font.BOLD, 36);
            g.setFont(font);

            String text = "Game Over: " + (int) score;

            FontMetrics fm = g.getFontMetrics(font);
            int x = (boardWidth - fm.stringWidth(text)) / 2;
            int y = (boardHeight - fm.getHeight()) / 2 + fm.getAscent();

            g.drawString(text, x, y);

            g.setColor(Color.BLUE);
            Font font2 = new Font("Arial", Font.PLAIN, 28);
            g.setFont(font2);

            String text2 = "Your Score: " + (int) score;
            int x2 = x + 30;
            int y2 = y + 30;

            g.drawString(text2, x2,y2);
            return;
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move(){
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; // 2 pipes, so 2*0.5 = 1; 1 point for each set of pipes.
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.y > boardHeight){
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width &&  // a's top left corner doesn't reach b's top right corner.
               a.x + a.width > b.x &&  // a's top right corner passes b's top left corner.
               a.y < b.y + b.height && // a's top left corner doesn't reach b's bottom left corner.
               a.y + a.height > b.y;   // a's bottom left corner passes b's top left corner
               // Confused 😵‍💫😖😵
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // Jump (SPACE)
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocityY = -9;
        }

        // Restart (R or r)
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restartGame();
        }
    }

    public void restartGame() {
    bird.y = birdY;
    velocityY = 0;
    pipes.clear();
    score = 0;
    gameOver = false;

    gameLoop.start();
    placePipesTimer.start();
}



    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
