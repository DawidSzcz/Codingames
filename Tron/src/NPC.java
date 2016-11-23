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

    public Point[] getPossibilities()
    {
        return body.peek().getMoves();
    }
    public Point getHead()
    {
        return body.peek();
    }
}
