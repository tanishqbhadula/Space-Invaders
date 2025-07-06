import java.awt.*;

public class Block {
    int x, y;
    int width, hieght;
    Image img;
    Boolean alive = true; // for ALIENS
    Boolean used = false; // for BULLETS

    Block(int x, int y, int width, int hieght, Image img){
        this.x = x;
        this.y = y;
        this.width = width;
        this.hieght = hieght;
        this.img = img;
    }
}
