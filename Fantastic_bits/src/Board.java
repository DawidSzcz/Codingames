import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by kazik on 27.11.16.
 */
class Board
{
    private List<Snuffle> snufs = new LinkedList<>();
    private NPCs allies= new NPCs();

    public void insert(){

        int entityId = Context.in.nextInt(); // entity identifier
        String entityType = Context.in.next(); // "WIZARD", "OPPONENT_WIZARD" or "SNAFFLE" (or "BLUDGER" after first league)
        int x       = Context.in.nextInt();
        int y       = Context.in.nextInt();
        int vx      = Context.in.nextInt();
        int vy      = Context.in.nextInt();
        int state   = Context.in.nextInt();
        switch(entityType) {
            case "WIZARD":
                allies.update(entityId, x, y, vx, vy, state);
                break;
            case "SNAFFLE":
                snufs.add(new Snuffle(entityId, x, y, vx, vy));
                break;
            case "BLUDGER":
            case "OPPONENT_WIZARD":
                allies.addEnemy(x, y, vx, vy);
            default:
                System.err.println("dfault");
        }
    }
    public void makeMove()
    {
        allies.makeMove(snufs);
        snufs.clear();
    }
}
