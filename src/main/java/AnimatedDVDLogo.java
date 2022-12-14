import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

@SuppressWarnings("serial")
public class AnimatedDVDLogo extends Canvas implements Runnable {

    private Thread thread;
    private JFrame frame;
    private Random ran;
    public final static int WIDTH = 800, HEIGHT = 600;
    private static String title = "DVD Logo";
    private static boolean running = false;
    private BufferedImage image;

    public AnimatedDVDLogo() {
        this.frame = new JFrame();
        this.frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File("./res/dvdvideo.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AnimatedDVDLogo display = new AnimatedDVDLogo();
        display.frame.setTitle(title);
        display.frame.add(display);
        display.frame.pack();
        display.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display.frame.setLocationRelativeTo(null);
        display.frame.setResizable(false);
        display.frame.setVisible(true);
        display.start();
    }

    public synchronized void start() {
        running = true;
        this.thread = new Thread(this, "Display");
        this.thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            this.thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1e9 / 60;
        double delta = 0;
        int frames = 0;

        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1) {
                update();
                delta--;
                render();
                frames++;
            }
            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                this.frame.setTitle(title + " | " + frames + " FPS");
                frames = 0;
            }
        }
        stop();
    }

    int x;
    int y;
    int y2;
    int limit;
    int limit2;
    int r, gg, b;
    int timer = 1000 / 50;
    Color color;
    boolean rightDown = true, up = false, left = false, leftUp = false;

    public void render() {
        if(y2 == 0) {
            ran = new Random();
            y2 = ran.nextInt(HEIGHT) - 40;
            y = y2;
            color = Color.white;
        }
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        //Background
        g.setColor(Color.black);
        g.fillRect(-1, -1, WIDTH, HEIGHT);

        //Draw here!
        g.setColor(color);
        g.setFont(new Font("Comic Sans MS", 10, 20));

        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if(
                    image.getRGB(x, y) == Color.black.getRGB() ||
                    image.getRGB(x, y) == Color.white.getRGB() ||
                    image.getRGB(x, y) == Color.gray.getRGB() ||
                    image.getRGB(x, y) == Color.darkGray.getRGB() ||
                    image.getRGB(x, y) == Color.lightGray.getRGB()) {

                    img.setRGB(x,y,color.getRGB());
                }
            }
        }

        g.drawImage(img, x, y,null);
        g.dispose();

        bs.show();
        //Up collisions
        if (y >= HEIGHT - 127 && limit2 != 2) {
            left = false;
            leftUp = false;
            rightDown = false;
            up = true;
            limit = 0;
            getColor();
        } else if (y >= HEIGHT - 127 && limit2 == 2) {
            left = true;
            leftUp = false;
            rightDown = false;
            up = false;
            limit = 1;
            getColor();
        }
        //Left collisions
        if (x >= WIDTH - 234 && limit2 != 2 && limit2 != 1) {
            rightDown = false;
            leftUp = false;
            up = false;
            left = true;
            limit = 1;
            getColor();
        } else if (x >= WIDTH - 234 && limit2 == 2) {
            rightDown = true;
            leftUp = false;
            up = false;
            left = false;
            limit = 3;
            getColor();
        }
        //Up2 collisions
        if (y <= 0 && limit2 != 0 && limit2 != 3 && limit2 != 2) {
            left = false;
            rightDown = false;
            up = false;
            leftUp = true;
            limit = 2;
            getColor();
        } else if (y <= 0 && limit2 == 0) {
            left = false;
            rightDown = true;
            up = false;
            leftUp = false;
            limit = 3;
            getColor();
        }
        //Down collisions
        if (x <= 0 && limit2 != 0 && limit2 != 3) {
            left = false;
            up = false;
            leftUp = false;
            rightDown = true;
            limit = 3;
            getColor();
        } else if (x <= 0 && limit2 == 0) {
            left = false;
            up = true;
            leftUp = false;
            rightDown = false;
            limit = 0;
            getColor();
        }

        limit2 = limit;

        if (rightDown) {
            x++;
            y++;
        }
        if (up) {
            x++;
            y--;
        }
        if (left) {
            x--;
            y--;
        }
        if (leftUp) {
            x--;
            y++;
        }
        timer--;
        if(timer <= 0) {
            timer = 1000 / 50;
        }
    }
    public void update() {}

    public void getColor() {
        r = ran.nextInt(256);
        gg = ran.nextInt(256);
        b = ran.nextInt(256);
        color = new Color(r, gg, b);
    }
}