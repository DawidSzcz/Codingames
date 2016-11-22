import java.util.Map;
import javafx.util.Pair;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.io.*;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.util.*;

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

class Player
{

    public static void main(String args[]) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in) );
        Board board = new Board();
        int N = 0, P;

        while (true) {
            if(! board.isInitialized()){
                StringTokenizer t = new StringTokenizer(reader.readLine());
                N = Integer.parseInt(t.nextToken());
                P = Integer.parseInt(t.nextToken());
                board.init(P, N);
            } else {
                reader.readLine();
            }

            for (int i = 0; i < N; i++) {
                board.insert(i, reader.readLine());
            }
            System.out.println(board.getMove());
        }
    }
}

class NPCs {
    private Ally me;
    private NPC[] npcs;
    private int p, n;
    private Move move;

    public NPCs(int P, int N, Board board)
    {
        n = N;
        p = P;
        me = new Ally(P, board);
        npcs = new NPC[N];
        for(int i = 0; i < N; i++) {
            if (i != P) {
                npcs[i] = new Enemy(i, board);
            } else {
                npcs[i] = me;
            }
        }
    }
    public void insert(int id, Point point) throws Exception {
        if(point == null) {
            while (id < this.n - 1) {
                npcs[id] = npcs[++id];
            }
            npcs[id] = null;
            n -= 1;
        } else {
            npcs[id].insertPos(point);
        }
    }

    public boolean defeated(int i){
        return npcs[i] == null && i != p;
    }

    public String toString()
    {
        String ret = "";
        for(NPC npc : npcs){
            ret += npc.toString();
        }
        return ret;
    }
    public String getMove(Board board)
    {
        if(this.move == null) {
            this.move = new Move(board, this.p, this.n);
            this.makeMove(this.move, 0);
        }
        Leaf best = this.move.findBest();
        System.err.println("Move " + this.move);
        return this.me.makeMove(best.findMove(this.p));
    }
    private void makeMove(Move move, int i)
    {
        if(i >= 2 *this.n){

        } else {
            npcs[i%this.n].makeMove(move, i);
            for(Move nextMove : move.nextMoves) {
                this.makeMove(nextMove, i + 1);
            }
        }
    }
}

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

/**
 * Created by kazik on 22.11.16.
 */
public class MinMaxTree {
    Map<Integer, Move[]>= new HashMap();
}
class Ally extends NPC {

    private String currentMove = "RIGHT";
    public Ally(int id, Board board)
    {
        super(id, board);
    }

    protected String getType()
    {
        return "ALLY";
    }
    public Point[] getPossibilities()
    {
        return body.peek().getMoves(this.currentMove);
    }

    public String makeMove(Point p)
    {
        Point myPos = body.peek();
        if(myPos.x < p.x){
            this.currentMove = "RIGHT";
        }
        if(myPos.x > p.x){
            this.currentMove = "LEFT";
        }
        if(myPos.y > p.y){
            this.currentMove = "UP";
        }
        if(myPos.y < p.y){
            this.currentMove = "DOWN";
        }
        return this.currentMove;
    }
}

class Board {
    int[][] board = new int[20][30];
    private NPCs npcs = null;

    public boolean isInitialized(){
        return  npcs != null;
    }

    public void init(int P, int N){
        npcs = new NPCs(P, N, this);
    }
    public void insert(int id, String row) throws Exception
    {
        if(!npcs.defeated(id)) {
            StringTokenizer t = new StringTokenizer(row);
            t.nextToken();
            t.nextToken();
            this.insert(id, Integer.parseInt(t.nextToken()), Integer.parseInt((t.nextToken())));
        }
    }
    private void insert(int id, int x, int y) throws Exception
    {
        if(x == -1){
            this.defeat(id);
        } else {
            npcs.insert(id, new Point(x, y));
            board[y][x] = id+1;
        }
    }

    private void defeat(int id) throws Exception
    {
        npcs.insert(id, null);
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 30; j++){
                if(board[i][j] == id + 1){
                    board[i][j] = 0;
                }
            }
        }
    }

    public String toString()
    {
        String r = "";
        for(int[] row : board){
            for(int x : row){
                r +=x;
            }
            r += '\n';
        }
        return r;
    }

    public String getMove()
    {
        return npcs.getMove(this);
    }

    public boolean isBusy(Point p)
    {
        return !(p.x >= 0 && p.x < 30 && p.y >= 0 && p.y < 20 && board[p.y][p.x] == 0);
    }

}
class Point
{
    public int x,  y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public int manDist(Point p)
    {
        return Math.abs(this.x - p.x) + Math.abs(this.y - p.y);
    }

    public Point[] getMoves()
    {
        Point[] points = {new Point(x + 1, y), new Point(x - 1, y), new Point(x, y + 1),new Point(x, y - 1)};
        return points;
    }
    public boolean equals(Point p)
    {
        return this.x == p.x && this.y == p.y;
    }

    public Point[] getMoves(String current)
    {
        Point[] pairs = {new Point(x + 1, y), new Point(x - 1, y), new Point(x, y + 1),new Point(x, y - 1)};
        Point temp;
        switch(current){
            case "LEFT":
                temp = pairs[1];
                pairs[1] = pairs[0];
                pairs[0] = temp;
                break;
            case "DOWN":
                temp = pairs[2];
                pairs[2] = pairs[0];
                pairs[0] = temp;
                break;
            case "UP":
                temp = pairs[3];
                pairs[3] = pairs[0];
                pairs[0] = temp;
                break;
        }
        //System.err.println(pairs.toString());
        return pairs;
    }
    public String toString()
    {
        return "POINT: " + x + " " + y;
    }
}
