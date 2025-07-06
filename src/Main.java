import javax.swing.*;
public class Main {
    public static void main(String[] args) throws Exception{
        JFrame frame = new JFrame("space_invaders");

        // window size
        int tile = 32;
        int row = 16, column = 16;
        int boardHieght = tile * row; //512px
        int boardWidth = tile * column; //512px

        frame.setSize(boardWidth, boardHieght);
        frame.setResizable(false); // player cant change window size by dragging
        frame.setLocationRelativeTo(null); // sets default location to centre of screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // terminate program when user closes

        SpaceInvaders si = new SpaceInvaders();
        frame.add(si);
        frame.pack();
        si.requestFocus();
        frame.setVisible(true);
    }
}