import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

class Move {
    public int id;
    public Point move;
    public int score;
    protected static Board board;
    protected static int p;
    protected static int n;
    public Move previous;
    public List<Move> nextMoves = new LinkedList<Move>();
    protected int stepRemaining;

    public Move(Board board, int P, int N)
    {
        this.board = board;
        this.p = P;
        this.n = N;
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
        this.score = 0;
        this.stepRemaining = stepsRemain;
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
    public void insertNexts(Point[] possiilities, int stepsRemain)
    {
        for (Point point : possiilities) {
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
    public Leaf findBest()
    {
        Leaf best = null;
        for(Move move : nextMoves){
            Leaf t_b = move.findBest();
            if (t_b != null && (best == null || t_b.score > best.score)) {
                best = t_b;
            }
        }
        return best;
    }

    public String toString()
    {
        return this.getType() + this.move + " " + score + '\n' + (this.previous != null ? this.previous.toString() : "");
    }

    private String getType()
    {
        return "INNER[" + this.stepRemaining + "]: ";
    }
    public int bfs(Move[] points)
    {
        boolean[][] visited = new boolean[20][30];
        this.previous.markMadeFields(visited);
        int fields = 0, score = 0;
        Point p;
        int[] scores = new int[points.length];
        for(int i = 0; i < points.length; i++) {
            Stack<Point> stack = new Stack<Point>();
            stack.push(points[i].move);
            fields = 0;

            while (!stack.empty()) {
                p = stack.pop();
                if (!board.isBusy(p) && !visited[p.y][p.x]) {
                    if (p.equals(points[1])) {
                        return 0;
                    }
                    visited[p.y][p.x] = true;
                    fields++;
                    for (Point pt : p.getMoves()) {
                        stack.push(pt);
                    }
                }
            }
            scores[i] = fields;
        }
        for(int i = 0; i < points.length; i++) {
            score += points[i].id == this.p ? scores[i] : -scores[i];
        }
        return score;
    }
}
