import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kazik on 27.11.16.
 */
class NPCs
{
    private Defender defender = new Defender();
    private Striker striker = new Striker();
    private List<Point> enemies = new LinkedList<>();
    private int strikerId =-1;

    public void addEnemy(int x, int y, int vx, int vz)
    {
        this.enemies.add(new Point(x, y));
    }
    public void makeMove(List<Snuffle> snufs)
    {
        striker.makeMove(snufs, enemies);
        defender.makeMove(snufs, enemies);
        enemies.clear();
    }

    public void update(int id, int x, int y, int vx, int vy, int state) {
        if(strikerId == -1 || strikerId == id) {
            striker.update(x, y, vx, vy, state);
            strikerId = id;
        } else {
            defender.update(x, y, vx, vy, state);
        }
    }
}
