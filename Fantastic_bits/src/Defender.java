import java.util.List;

/**
 * Created by kazik on 29.11.16.
 */
class Defender extends Ally
{
    Point startPos;
    public Defender()
    {
        if(Context.myTeamId == 0) {
            startPos = new Point(1000, 3750);
        } else {
            startPos = new Point(15000, 3750);
        }
    }

    protected void moveToClosest(List<Snuffle> snufs) {
        double max = Context.defenderArea, t_m;
        Snuffle goal = null;
        for(Snuffle snuf : snufs) {
            if(snuf.isAviable() && (t_m = Context.myBase.dist(snuf.pos)) < max) {
                max = t_m;
                goal = snuf;
            }
        }

        if(goal != null) {
            goal.take();
            System.out.println("MOVE " + goal.pos.toString() + " 150");
        } else {
            System.out.println("MOVE " + this.startPos.toString() + " 150");
        }
    }

    protected String getType() {
        return "DEFENDER ";
    }
}
