import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

class Move {
    public int id;
    public Point move;
    public int score;
    protected static Board board;
    public Move previous;
    public Move previosFP;
    public List<Move> nextMoves = new LinkedList<Move>();
    protected int stepRemaining;

    public Move(Board board)
    {
        this.board = board;
        this.previous = null;
        this.score = 0;
        this.id = -1;
        this.move = new Point(-1, -1);
    }
    public Move(int id, Point point, Move prev, int stepsRemain)
    {
        this.id = id;
        this.move = point;
        this.previous = prev;
        this.previosFP = null;
        this.score = 0;
        this.stepRemaining = stepsRemain;

        Move temp = prev;
        System.err.println("insert move: " + id);
        while(temp != null) {
            System.err.println(id + " " + temp.id);
            if (temp.id == id) {
                this.previosFP = temp;
                System.err.println(previosFP.move);
                break;
            }
            temp = temp.previous;
        }
    }

    private boolean isBusy(Point p)
    {
        if(this.move.x == p.x && this.move.y == p.y){
            return true;
        }
        if(this.previous == null) {
            return false;
        }
        return this.previous.isBusy(p);
    }
    public void insertNexts(int id, Point[] possiilities, int stepsRemain)
    {

        for (Point point : possiilities) {
            System.err.println("POS" + point);
            if (!this.board.isBusy(point) && !this.isBusy(point)) {
                this.nextMoves.add(new Move(id, point, this, stepsRemain));
            }
        }
    }

    protected void markMadeFields(boolean[][] v)
    {
        if(previous == null){
            return;
        }
        v[this.move.y][this.move.x] = true;
        previous.markMadeFields(v);
    }

    public String toString()
    {
        return this.getType() + " id: " + this.id + " " +this.move  + '\n' + (this.previous != null ? this.previous.toString() : "");
    }

    private String getType()
    {
        return "INNER[" + this.stepRemaining + "]: ";
    }

    public void scoreTurn(Move[] lastTurn, int myId)
    {
        boolean[][] visited = new boolean[20][30];
        this.previous.markMadeFields(visited);
        int fields = 0;
        Point p;
        for(int i = 0; i < lastTurn.length; i++) {
            Stack<Point> stack = new Stack<Point>();
            stack.push(lastTurn[i].move);
            fields = 0;

            while (!stack.empty()) {
                p = stack.pop();
                if (!board.isBusy(p) && !visited[p.y][p.x]) {
                    if (p.equals(lastTurn[1].move)) {
                        this.score = 0;
                    }
                    visited[p.y][p.x] = true;
                    fields++;
                    for (Point pt : p.getMoves()) {
                        stack.push(pt);
                    }
                }
            }
            this.score += (myId == i) ? fields : -fields;
        }
    }
}
