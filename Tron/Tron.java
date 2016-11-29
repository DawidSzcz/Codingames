import javafx.util.Pair;
import java.io.*;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.util.*;

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
            //System.err.println(board);
            System.out.println(board.getMove());
        }
    }
}

class NPCs {
    private Ally me;
    private NPC[] npcs;
    private int p;
    private Move move;
    private Board board;

    public NPCs(int P, int N, Board board)
    {
        p = P;
        me = new Ally(P, board);
        npcs = new NPC[N];
        this.board = board;
        for(int i = 0; i < N; i++) {
            if (i != P) {
                npcs[i] = new Enemy(i, board);
            } else {
                npcs[i] = me;
            }
        }
    }
    public void insert(int id, Point point) throws Exception
    {
        if(point == null) {
            npcs[id] = null;
        } else {
            npcs[id].insertPos(point);
        }
    }

    public boolean aviable(int i)
    {
        return !(npcs[i] == null);
    }

    public String toString()
    {
        String ret = "";
        for (NPC npc : npcs) {
            if(npc != null) {
                ret += npc.toString();
            }
        }
        return ret;
    }
    public List<Point> getHeads() {
        List<Point> players= new LinkedList<>();
        players.add(me.getHead());
        for(int i = p+1; i< p+ this.npcs.length; i++) {
            if(this.aviable(i%this.npcs.length)) {
                players.add(this.npcs[i % this.npcs.length].getHead());
            }
        }
        return players;
    }

    public String makeMove()
    {
        int max = -1000000, t_m;
        Point best = null;
        List<Point> heads = this.getHeads();
        Point[] myPossibs = heads.get(0).getMoves();
        for(Point myMove : myPossibs){
            if(board.isBusy(myMove)) {
                t_m = -1000000;
            } else {
                Point prev = heads.get(0);
                heads.add(0, myMove);
                board.insert(0, myMove);
                if ((t_m = nextTurn(heads, 1)) > max) {
                    max = t_m;
                    best = myMove;
                }
                heads.set(0,prev);
                board.insert(-1, myMove);
            }
        }
        return me.makeMove(best);
    }

    private int nextTurn(List<Point> heads, int depth) {
        if(depth == 5) {
            return board.score(heads);
        } else {
            if(depth%heads.size() == 0) {
                int max = -100000, t_m;

                Point prev = heads.get(depth%(heads.size()));
                Point[] nextMoves = heads.get(depth%heads.size()).getMoves();
                for(Point nextMove : nextMoves) {
                    if(board.isBusy(nextMove)) {
                        continue;
                    } else {
                        heads.add(depth%heads.size(), nextMove);
                        board.insert(0, nextMove);
                        if ((t_m = nextTurn(heads, depth + 1)) > max) {
                            max = t_m;
                        }
                        board.insert(-1, nextMove);
                    }
                }

                heads.set(0, prev);
                heads.set(0, prev);
                return max;
            } else {
                int min = 100000, t_m;
                Point[] nextMoves = heads.get(depth%heads.size()).getMoves();
                Point prev = heads.get(depth%(heads.size()));
                for(Point nextMove : nextMoves) {
                    if(board.isBusy(nextMove)) {
                        continue;
                    } else {
                        heads.add(depth%heads.size(), nextMove);
                        board.insert(0, nextMove);
                        if ((t_m = nextTurn(heads, depth + 1)) < min) {
                            min = t_m;
                        }
                        board.insert(-1, nextMove);
                    }
                }
                heads.set(0, prev);

                return min;
            }
        }
    }
    private Point[] getNextMove(List<Point> moves)
    {
        return moves.get(moves.size()-1).getMoves();
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

    public Point[] getPossibilities()
    {
        return body.peek().getMoves();
    }
    public Point getHead()
    {
        return body.peek();
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
class Ally extends NPC
{

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
        if (myPos.x < p.x) {
            this.currentMove = "RIGHT";
        }
        if (myPos.x > p.x) {
            this.currentMove = "LEFT";
        }
        if (myPos.y > p.y) {
            this.currentMove = "UP";
        }
        if (myPos.y < p.y) {
            this.currentMove = "DOWN";
        }
        return this.currentMove;
    }

    public String getMove()
    {
        return this.currentMove;
    }
}

class Board
{
    int[][] board = new int[20][30];
    private NPCs npcs = null;
    private Queue<Point> myQueue = new LinkedList<Point>();
    private Queue<Point> eQueue = new LinkedList<Point>();

    public boolean isInitialized()
    {
        return npcs != null;
    }

    public void init(int P, int N)
    {
        npcs = new NPCs(P, N, this);
        //mmt = new MinMaxTree(npcs, this, P, 2);
    }

    public void insert(int id, String row) throws Exception
    {
        if (npcs.aviable(id)) {
            StringTokenizer t = new StringTokenizer(row);
            int     x0 = Integer.parseInt(t.nextToken()),
                    y0 = Integer.parseInt(t.nextToken()),
                    x1 = Integer.parseInt(t.nextToken()),
                    y1 = Integer.parseInt(t.nextToken());

            //System.err.println(id + ": (" + x0 + ", " + y0 + ") (" + x1 + ", " + y1 + ")");
            this.insert(id, x0, y0);
            this.insert(id, x1, y1);
        }
    }

    private void insert(int id, int x, int y) throws Exception
    {
        if (x == -1) {
            this.defeat(id);
        } else {
            npcs.insert(id, new Point(x, y));
            board[y][x] = id + 1;
        }
    }
    public void insert(int id, Point p)
    {
        board[p.y][p.x] = id + 1;
    }

    private void defeat(int id) throws Exception
    {
        if(npcs.aviable(id)) {
            //System.err.println("DEFEAT");
            npcs.insert(id, null);
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 30; j++) {
                    if (board[i][j] == id + 1) {
                        board[i][j] = 0;
                    }
                }
            }
        }
    }

    public String toString()
    {
        String r = "";
        for (int[] row : board) {
            for (int x : row) {
                r += x;
            }
            r += '\n';
        }
        return r;
    }

    public String getMove()
    {
        return npcs.makeMove();
    }

    public boolean isBusy(Point p)
    {
        return !(p.x >= 0 && p.x < 30 && p.y >= 0 && p.y < 20 && board[p.y][p.x] == 0);
    }

    public int score(List<Point> heads)
    {
        //System.err.print("Enemies " + enemies + "\n");
        //System.err.print("MyMoves " + myMoves + "\n");
        boolean[][] visited = new boolean[20][30];
        int     score = 0,
                sSize = 0;
        Point move = null;

        for(Point p : heads.get(0).getMoves()) {
            myQueue.add(p);
        }
        for(int i = 1; i < heads.size(); i++) {
            for (Point p : heads.get(i).getMoves()) {
                myQueue.add(p);
            }
        }

        while (!eQueue.isEmpty() || !myQueue.isEmpty()) {
            sSize = myQueue.size();
            for (int i = 0; i < sSize; i++) {
                move = myQueue.poll();
                if (!this.isBusy(move) && !visited[move.y][move.x]) {
                    visited[move.y][move.x] = true;
                    score++;
                    for (Point pt : move.getMoves()) {
                        myQueue.add(pt);
                    }
                }
            }
            sSize = eQueue.size();
            for (int i = 0; i < sSize; i++) {
                move = eQueue.poll();
                if (!this.isBusy(move) && !visited[move.y][move.x]) {
                    visited[move.y][move.x] = true;
                    score--;
                    for (Point pt : move.getMoves()) {
                        eQueue.add(pt);
                    }
                }
            }
        }
        return score;
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
