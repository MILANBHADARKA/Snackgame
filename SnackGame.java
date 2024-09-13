import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;


public class SnackGame extends Canvas implements ActionListener, KeyListener {

    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    Dialog d;
    boolean easy = false;
    boolean hard = false;

    int boardwidth;
    int boardheight;
    int tilesize = 25; // size of each tile in the game board
    int velocityX;
    int velocityY;
    boolean gameOver = false;

    Color snackcolor;
    Color BGcolor;
    Color textcolor;
    Color over;
    boolean black = true;

    int speed = 100;


    // snack
    Tile snackHead;
    ArrayList<Tile> snackBody;

    // food
    Tile food;
    Random random;


    Timer gameLoop;

    SnackGame() {
        boardwidth = 600;
        boardheight = 600;

        BGcolor = Color.BLACK;
        snackcolor = Color.GREEN;
        textcolor = Color.WHITE;
        over = Color.RED;

        setSize(boardwidth, boardheight);
        // setBackground(new Color(27, 18, 18));
        setBackground(BGcolor);
        addKeyListener(this);
        setFocusable(true);         //Ensures the canvas receives keyboard events.

        snackHead = new Tile(5, 5);
        snackBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placefood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(speed, this);

        createDifficultyDialog();

        gameLoop.start();

    }

    private void createDifficultyDialog() {
        // Dialog for difficulty selection
        d = new Dialog(new Frame(), "Select Difficulty", true);
        d.setLayout(new FlowLayout());
        
        Button b1 = new Button("Easy");
        Button b2 = new Button("Hard");
    
        
        b1.addActionListener(e -> {
            easy = true;
            hard = false;
            speed = 100;  
            gameLoop.setDelay(speed); 
            d.dispose(); 
        });
    
        // Hard button action listener
        b2.addActionListener(e -> {
            easy = false;
            hard = true;
            speed = 50;  
            gameLoop.setDelay(speed);
            d.dispose();  
        });

        d.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    

        d.add(b1);
        d.add(b2);
        d.setSize(200, 100);
        // d.setLocationRelativeTo(null);  
        d.setVisible(true);  
    }

    @Override
    public void paint(Graphics g) {
        draw(g);
    }

    /**
     * @param g
     */
    public void draw(Graphics g) {

        setBackground(BGcolor);

        //food
        g.setColor(Color.RED);
        g.fill3DRect(food.x * tilesize, food.y * tilesize, tilesize, tilesize, true);

        // draw the snack head
        g.setColor(snackcolor);
        g.fill3DRect(snackHead.x * tilesize, snackHead.y * tilesize, tilesize, tilesize, true);

        // snack body
        for (int i = 0; i < snackBody.size(); i++) {
            Tile snackPart = snackBody.get(i);
            g.fill3DRect(snackPart.x * tilesize, snackPart.y * tilesize, tilesize, tilesize, true);
        }

        // score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            // g.setColor(Color.RED);
            g.setColor(over);
            g.drawString("Game Over: " + String.valueOf(snackBody.size()), tilesize, tilesize);  
        } else {
            // g.setColor(Color.WHITE);
            g.setColor(textcolor);
            g.drawString("Score: " + String.valueOf(snackBody.size()), tilesize - 16, tilesize);
        }

    }

    public void placefood() {
        food.x = random.nextInt(boardwidth / tilesize);     
        food.y = random.nextInt(boardheight / tilesize);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        // Move the snack body
        for (int i = snackBody.size() - 1; i > 0; i--) {
            Tile prevSnackPart = snackBody.get(i - 1);
            Tile snackPart = snackBody.get(i);
            snackPart.x = prevSnackPart.x;
            snackPart.y = prevSnackPart.y;
        }

        // Move the first body part to the head's previous position
        if (!snackBody.isEmpty()) {
            Tile firstBodyPart = snackBody.get(0);
            firstBodyPart.x = snackHead.x;
            firstBodyPart.y = snackHead.y;
        }

        // Update the snack head position
        snackHead.x += velocityX;
        snackHead.y += velocityY;

        // game oVer
        for (int i = 0; i < snackBody.size(); i++) {
            Tile snackPart = snackBody.get(i);
            if (collision(snackHead, snackPart)) {
                gameOver = true;
            }
        }

        if (snackHead.x * tilesize < 0 || snackHead.x * tilesize >= boardwidth || snackHead.y * tilesize < 0 || snackHead.y * tilesize >= boardheight) {
            gameOver = true;
        }

        // Eat food
        if (collision(snackHead, food)) {
            snackBody.add(new Tile(food.x, food.y));
            placefood();
        }

    }

    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        } else {
            gameLoop.stop();
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_T){
            if (black){
                BGcolor = Color.BLACK;
                snackcolor = Color.GREEN;
                textcolor = Color.WHITE;
                over = Color.RED;
                black = false;

                repaint();
            }else{
                BGcolor = Color.WHITE;
                snackcolor = Color.BLACK;
                textcolor = Color.BLACK;
                over = Color.BLUE;
                black = true;

                repaint();
            }
        } else if(e.getKeyCode() == KeyEvent.VK_W){
            if(speed > 50){
                speed -= 10;
                gameLoop.setDelay(speed);
            }

        } else if(e.getKeyCode() == KeyEvent.VK_S){
            if(speed < 150) {   
                speed += 10;
                gameLoop.setDelay(speed);
            }

        }

        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameOver = false;
                snackHead = new Tile(5, 5);
                snackBody.clear();
                velocityX = 0;
                velocityY = 0;
                speed = 100;

                gameLoop.setDelay(speed);
                placefood();

                gameLoop.start();
            }
        }

    }

    // not need
    public void keyTyped(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {
        Frame frame = new Frame("Snack Game");
        SnackGame game = new SnackGame();

        frame.add(game);
        frame.pack();
        frame.setSize(650, 650);
        frame.setResizable(false);

        Image fevicon = Toolkit.getDefaultToolkit().getImage("./image/download.jpeg");
        frame.setIconImage(fevicon);


        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        frame.setVisible(true);

    }
}






//         gameLoop.scheduleAtFixedRate(new TimerTask() {
//             public void run() {
//                 if (!gameOver) {
//                     move();
//                     repaint();
//                 } else {
//                     gameLoop.cancel();
//                 }
//             }
//         }, 0, 100);


