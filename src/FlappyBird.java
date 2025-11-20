import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel{
    int larguraBorda = 360;
    int alturaBorda = 640;

    //IMAGEM
    Image birdImage;
    Image backgroundImage;
    Image bottomPipeImage;
    Image topPipeImage;
    Image gameOverImage;

    //PASSARO
    int birdX = larguraBorda/8;
    int birdY = alturaBorda/2;
    int birdWidth = 34;
    int birdHeight = 24;

    //Button Restart
    JButton restartButton;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image image;

        Bird(Image image) {
            this.image = image;
        }
    }

    //PIPES
    int pipeX = larguraBorda;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image image;
        boolean passed = false;

        Pipe(Image image) {
            this.image = image;
        }
    }

    //Game Logic
    Bird bird;
    int velocityX = -4;
    double velocityY = 0;
    double gravity = 0.8;

    ArrayList<Pipe> pipes;

    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;

    boolean gameOverShown = false;

    double counter = 0;


    //Constructor
    FlappyBird() {
        setPreferredSize(new Dimension(larguraBorda, alturaBorda));
        setLayout(null);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                FlappyBird.this.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                FlappyBird.this.keyReleased(e);
            }

            @Override
            public void keyTyped(KeyEvent e) {
                FlappyBird.this.keyTyped(e);
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                jump();
            }
        });

        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        gameOverImage = new ImageIcon(getClass().getResource("./gameover.png")).getImage();

        bird = new Bird(birdImage);

        pipes = new ArrayList<Pipe>();
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();
        gameLoop = new Timer(1000/60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FlappyBird.this.actionPerformed(e);
            }
        });
        gameLoop.start();

        // Iniciando o botão de restart
        restartButton = new JButton(new ImageIcon(getClass().getResource("./restart.png")));
        restartButton.setBounds(larguraBorda / 2 - 137, alturaBorda / 2 - 37, 274, 75);
        restartButton.setVisible(false);
        restartButton.setFocusable(false);
        
        // Removendo borda, fundo e highlight do botão
        restartButton.setBorderPainted(false);
        restartButton.setContentAreaFilled(false);
        restartButton.setFocusPainted(false);

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        add(restartButton);
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void placePipes(){
        int randomPypeY = (int) (pipeY - pipeHeight/4 - (Math.random() * pipeHeight/2));
        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPypeY;

        int oppeningSpace = alturaBorda/4;
        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + pipeHeight + oppeningSpace;
        
        pipes.add(topPipe);
        pipes.add(bottomPipe);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, larguraBorda, alturaBorda, null);
        g.drawImage(bird.image, bird.x, bird.y, bird.width, bird.height, null);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + (int)counter, 10, 20);

        if(gameOver){
            // Escurecer o fundo

            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, larguraBorda, alturaBorda);

            //Inserir a image de game over
            // imagem possui 701 x 258, por isso será preciso redim

            double proportion = 2;
            int  gameOverWidth = (int)(701 / proportion);
            int  gameOverHeight = (int)(258 / proportion);
            g.drawImage(gameOverImage, (larguraBorda - gameOverWidth)/2, 100, gameOverWidth, gameOverHeight, null);

            // Botão de restart
            restartButton.setVisible(true);
            
            // Informa que o Game over já foi mostrado
            gameOverShown = true;
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);


        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                counter += 0.5;
            }
        }

        if (bird.y + bird.height >= alturaBorda) {
            gameOver = true;
        }

        if (bird.y <= 0) {
            gameOver = true;    
        }

        
    }

    public void collision(){
        Rectangle birdRect = new Rectangle(bird.x, bird.y, bird.width, bird.height);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            Rectangle pipeRect = new Rectangle(pipe.x, pipe.y, pipe.width, pipe.height);
            if (birdRect.intersects(pipeRect)) {
                gameOver = true;
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        collision();

        if(gameOver && !gameOverShown){
            gameLoop.stop();
            placePipesTimer.stop();
        }

        
    }

    public void restartGame() {
    //Resetar o bird
    bird.y = birdY;
    velocityY = 0;

    //Resetar os pipes
    pipes.clear();

    //Resetar o contador
    counter = 0;

    //Resetar o game over
    gameOver = false;
    gameOverShown = false;

    //Esconder o botão de restart
    restartButton.setVisible(false);

    //Reiniciar os timers
    gameLoop.start();
    placePipesTimer.start();

    repaint();
    }

   
    public void jump() {
        //Garantir que o pulo só ocorra se o jogo não estiver acabado
        if (!gameOver) {
            velocityY = -7; 
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();
        }
        
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                restartGame();
            }
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}
}