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

    //PASSARO
    int birdX = larguraBorda/8;
    int birdY = alturaBorda/2;
    int birdWidth = 34;
    int birdHeight = 24;

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


    //Game Logic
    Bird bird;

    FlappyBird() {
        setPreferredSize(new Dimension(larguraBorda, alturaBorda));
         
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
    
        bird = new Bird(birdImage);
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, larguraBorda, alturaBorda, null);
        g.drawImage(bird.image, bird.x, bird.y, bird.width, bird.height, null);
    }
}
