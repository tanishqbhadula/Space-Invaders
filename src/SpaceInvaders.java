import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    int tile = 32;
    int row = 16, column = 16;
    int boardHieght = tile * row; //512px
    int boardWidth = tile * column; //512px
    Image shipImg;
    Image alien_white, alien_cyan, alien_yellow, alien_pink;
    ArrayList<Image> alienImgArray;
    javax.swing.Timer gameLoop; // because java.util also has a "timer"

    // ship
    Block ship;
    int shipWidth = tile*2, shipHieght = tile; // 64 x 32
    int shipX = (tile * column / 2) - tile, shipY = boardHieght - (2*tile);
    int shipVelX = tile;

    // aliens
    ArrayList<Block> alienArray;
    int alienWidth = tile*2, alienHieght = tile; // 64 x 32
    int alienX = tile, alienY = tile;
    int alienRows = 2, alienColumns = 3;
    int alienCount = 0;
    int alienVelX = 1; // move 1px sideways every cycle

    // bullets
    ArrayList<Block> bulletArray;
    int bulletWidth = tile/8, bulletHieght = tile/2;
    int bulletVelY = -10; // 10px upwards

    int score = 0;
    boolean gameOver = false;

    SpaceInvaders() {
        setPreferredSize(new Dimension(boardWidth, boardHieght));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        // get images into code
        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alien_white = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alien_cyan = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alien_pink = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alien_yellow = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alien_white); alienImgArray.add(alien_cyan); alienImgArray.add(alien_pink); alienImgArray.add(alien_yellow);

        ship = new Block(shipX, shipY, shipWidth, shipHieght, shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();
        
        // game loop timer
        gameLoop = new Timer(1000/60, this); // 1000ms = 1s, div by 60 means 60 checks per second ie 60FPS
        createAliens();
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        // ship
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.hieght, null);

        // aliens
        for(Block alien : alienArray){
            if(alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.hieght, null);
            }
        }

        // bullet
        g.setColor(Color.white);
        for(Block bullet : bulletArray) {
            if(!bullet.used) {
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.hieght);
            }
        }

        // score
        g.setFont(new Font("Arial   ",Font.PLAIN, 32));
        if(gameOver){
            g.drawString("GAME OVER : " + String.valueOf(score), 10, 35);
            g.setFont(new Font("Arial   ",Font.PLAIN, 26));
            g.drawString("Press ANY key to restart", 10, 60);
        }
        else{
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    public void move() {  // controls alien and bullet movement
        // ALIEN
        for(int i = 0; i<alienArray.size(); i++){
            Block alien = alienArray.get(i);
            if(alien.alive) {
                alien.x += alienVelX;
            }

            // when alein at border
            if(alien.x + alien.width >= boardWidth || alien.x <= 0) {
                alienVelX *= -1;
                alien.x += alienVelX*2; // so that if turn around we dont get stuck in a constant loop of alien at border

                //move down by 1 tile size
                for(Block aln : alienArray){
                    aln.y += alienHieght;
                }
            }

            // check for game over
            if(alien.y >= ship.y){
                gameOver = true;
            }
        }

        // BULLET
        for(int i = 0; i<bulletArray.size(); i++){
            Block bullet = bulletArray.get(i);

            if(!bullet.used)
                bullet.y += bulletVelY;

            // collision
            for(int j = 0; j<alienArray.size(); j++){
                Block alien = alienArray.get(j);
                if(!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score+=100; // each alien = 100 points
                }
            }

            // if bullet goes past screen then remove it from memory(bullet array)
            while(!bulletArray.isEmpty() && (bulletArray.getFirst().used || bulletArray.getFirst().y < 0)){
                bulletArray.removeFirst();
            }

            // NEXT LEVEL
            if(alienCount == 0) {
                score+=150; // bonus points for level clear
                // next level - inc row and col by 1
                alienColumns = Math.min(alienColumns+1, column/2 - 2); // max = 16/2 - 2 = 8-2 = 6 cols
                alienRows = Math.min(alienRows+1, row-6); // max = 16-6 = 10 rows
                alienArray.clear();
                bulletArray.clear();
                alienVelX = 1;
                createAliens();
            }
        }
    }

    private void createAliens() {
        Random random = new Random();
        for(int row = 0; row<alienRows; row++) {
            for(int col = 0; col<alienColumns; col++) {
                int alienIdx = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                        alienX + col*alienWidth,
                        alienY + row*alienHieght,
                        alienWidth,
                        alienHieght,
                        alienImgArray.get(alienIdx)
                );
                alienArray.add(alien);
            }
            alienCount = alienArray.size();
        }
    }

    // Collision detection for bullet and alien
    public boolean detectCollision(Block alien, Block bullet) {
        return alien.x < bullet.x + bullet.width &&
                alien.x + alien.width > bullet.x &&
                alien.y < bullet.y + bullet.hieght &&
                alien.y + alien.hieght > bullet.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); // check every 16.6 ms
        if(gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) {
        if(gameOver) { // press any key to restart
            ship.x = shipX; // reset all var
            score = 0;
            alienArray.clear();
            bulletArray.clear();
            alienVelX = 1;
            alienColumns = 3;
            alienRows = 2;
            gameOver = false;
            createAliens();
            gameLoop.start();
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelX >= 0) {
            ship.x -= shipVelX; // move LEFT
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + shipVelX <= boardWidth - tile*2) {
            ship.x += shipVelX; // move RIGHT
        }
        else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            Block bullet = new Block(
                    ship.x + ship.width*15/32,
                    ship.y,
                    bulletWidth,
                    bulletHieght,
                    null
            );
            bulletArray.add(bullet);
        }
    }
}
