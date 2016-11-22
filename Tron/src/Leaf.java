import java.util.Stack;

class Leaf extends Move{
    public Leaf(int id, Point point, Move prev, int stepsRemain)
    {
        super(id, point, prev, stepsRemain);
        int score = this.score();
        //System.err.println("BFS: " + score);

        this.score = score();
    }
    public String toString()
    {
        return super.toString() +
                "\n*************************************";
    }
    private String getType()
    {
        return "LEAF[" + this.stepRemaining + "]: ";
    }

    private int score()
    {
        Move[] ms= null;
        if(this.n == 2){
            ms = new Move[]{this.previous, this};
        }
        if(this.n == 3){
            ms = new Move[]{this.previous, this.previous, this};
        }
        if(this.n == 4){
            ms = new Move[]{this.previous, this.previous, this.previous, this};
        }
        return bfs(ms);
    }
    public Leaf findBest()
    {
        if(this.stepRemaining == 0) {
            return this;
        }
        return super.findBest();
    }

    public Point findMove(int p)
    {
        Point move = null;
        Move m = this;
        while(m.previous != null)
        {
            if(p == m.id) {
                move = m.move;
            }
            m = m.previous;
        }
        return move;
    }
}
