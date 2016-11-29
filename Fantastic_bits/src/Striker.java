import java.util.Collection;
import java.util.List;

/**
 * Created by kazik on 27.11.16.
 */
class Striker extends Ally
{
    protected void moveToClosest(List<Snuffle> snufs)
    {
        System.err.println(snufs);
        double max = 1000000, t_m;
        Snuffle goal = null;
        for(Snuffle snuf : snufs) {
            if(snuf.isAviable() && (t_m = this.dist(snuf)) < max) {
                max = t_m;
                goal = snuf;
            }
        }
        if(goal != null) {
            goal.take();
            System.out.println("MOVE " + goal.pos.toString() + " 150");
        } else {
            System.out.println("MOVE " + Context.myBase.toString() + " 150");
        }
    }

    protected String getType() {
        return "STRIKER ";
    }
}
