import java.util.Stack;

abstract class NPC
{
    protected Stack<Point> body = new Stack<Point>();
    protected abstract String getType();
    public int id;
    protected Board board;

    public NPC(int i, Board board)
    {
        this.id = i;
        this.board = board;
    }
    public String toString()
    {
        Point p = body.peek();
        return this.getType() + ": [" + p.x + ", " + p.y + "]\n";
    }

    public void insertPos(Point p) throws Exception {
        //System.err.println("add " + this.getType() + "(" + this.id +"): "+ x + " " + y);
        body.push(p);
    }

    public void makeMove(Move move, int remainig)
    {
        move.insertNexts(this.getPossibilities(move), remainig);
    }

    private Point[] getPossibilities(Move m)
    {
        while(m.previous != null){
            if(m.id == this.id) {
                return m.move.getMoves();
            } else {
                m = m.previous;
            }
        }
        return this.body.peek().getMoves();
    }
}
