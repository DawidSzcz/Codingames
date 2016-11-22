import javafx.util.Pair;

/**
 * Created by kazik on 17.11.16.
 */
class Enemy extends NPC
{
    public Enemy(int id, Board board)
    {
        super(id, board);
    }

    /*public void insertPos(Point p) throws Exception
    {
        if(!body.empty()) {
            this.currentMove = this.body.peek().direction(p.x, p.y);
        }
        super.insertPos(p);
    }*/

    protected String getType()
    {
        return "ENEMY";
    }
}
