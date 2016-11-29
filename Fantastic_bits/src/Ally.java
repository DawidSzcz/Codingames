import java.util.LinkedList;
import java.util.List;

/**
 * Created by kazik on 29.11.16.
 */
abstract class Ally extends Obj
{
    int state;

    protected abstract void moveToClosest(List<Snuffle> snufs);

    public void makeMove(List<Snuffle> snufs, List<Point> enemies)
    {
        if(this.isCurring()) {
            System.out.println("THROW " + this.bestThrow(enemies).toString() + " 500");
        } else {
            this.moveToClosest(snufs);
        }
    }
    public void update(int x, int y, int vx, int vy, int state)
    {
        super.update(x, y, vx, vy);
        System.err.println(this.getType());
        this.state = state;
    }

    protected boolean isCurring()
    {
        return this.state == 1;
    }

    protected Point bestThrow(List<Point> enemies)
    {
        double max = 100000, t_m;
        Point best = Context.enemyBase;
        for(Point goal: Context.throwOrder) {
            for(Point enemy: enemies) {
                if(enemies.get(0).distFromLine(this.pos, goal) > Context.throwRadius &&
                   enemies.get(1).distFromLine(this.pos, goal) > Context.throwRadius) {
                    if((t_m = Context.enemyBase.dist(goal)) < max) {
                        max = t_m;
                        best = goal;
                    }
                }
            }
        }
        return best;
    }
}
