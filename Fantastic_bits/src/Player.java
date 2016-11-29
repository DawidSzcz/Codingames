import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Grab Snaffles and try to throw them through the opponent's goal!
 * Move towards a Snaffle and use your team id to determine where you need to throw it.
 **/
class Player {

    public static void main(String args[]) {
        Context.in = new Scanner(System.in);
        Context c= new Context(Context.in.nextInt()); // if 0 you need to score on the right of the map, if 1 you need to score on the left
        Board board = new Board();
        // game loop
        while (true) {
            int entities = Context.in.nextInt(); // number of entities still in game
            for (int i = 0; i < entities; i++) {
                board.insert();
            }
            board.makeMove();
        }
    }
}