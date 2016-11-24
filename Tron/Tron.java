import javafx.util.Pair;
import java.io.*;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.util.*;
/**
 * Created by kazik on 22.11.16.
 */
class Turn
{
}

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
    private int p, n;
    private Move move;
    private Board board;

    public NPCs(int P, int N, Board board)
    {
        n = N;
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
            n -= 1;
        } else {
            npcs[id].insertPos(point);
        }
    }

    public boolean defeated(int i)
    {
        return npcs[i] == null && i != p;
    }

    public String toString()
    {
        String ret = "";
        for (NPC npc : npcs) {
            ret += npc.toString();
        }
        return ret;
    }

    public String makeMove()
    {
        Point[] possibs = me.getPossibilities();
        int max = -1000000, t_m;
        Point best = null;
        List<Point> myMoves = new LinkedList<Point>();
        for(Point myMove : possibs){
            if(board.isBusy(myMove)) {
                t_m = -1000000;
            } else {
                myMoves.add(myMove);
                if ((t_m = nextTurn(myMoves, getEnemiesHeads(), 1)) > max) {
                    max = t_m;
                    best = myMove;
                }
                myMoves.remove(myMove);
            }
            //System.err.print(t_m + " " + myMove + "\n");
        }
        return me.makeMove(best);
    }
    private int nextTurn(List<Point> myMoves, List<List<Point>> enemies, int depth) {
        if(depth == 5) {
            return board.score(myMoves, enemies);
        } else {
            if(depth%(enemies.size()+1) == 0) {
                int max = -100000, t_m;
                Point[] nextMoves = this.getNextMove(myMoves);
                for(Point nextMove : nextMoves) {
                    if(board.isBusy(nextMove)) {
                        continue;
                    } else {
                        myMoves.add(nextMove);
                        if ((t_m = nextTurn(myMoves, enemies, depth + 1)) > max) {
                            max = t_m;
                        }
                        myMoves.remove(nextMove);
                    }
                }
                return max;
            } else {
                int min = 100000, t_m;
                List<Point> enemy = enemies.get((depth - 1) % enemies.size());
                //System.err.print("Enemy" + depth + enemy+ "\n");
                Point[] nextMoves;
                if(depth > enemies.size() +1) {
                    nextMoves = this.getNextMove(enemy);
                    for(Point nextMove : nextMoves) {
                        enemy.add(nextMove);
                        if((t_m = nextTurn(myMoves, enemies, depth+1)) < min) {
                            min = t_m;
                        }
                        //System.err.print(t_m + " " + nextMove + "\n");
                        enemy.remove(nextMove);
                    }
                } else {
                    Point head =  enemy.get(0);
                    nextMoves = head.getMoves();
                    enemy.remove(head);
                    for(Point nextMove : nextMoves) {
                        enemy.add(nextMove);
                        if((t_m = nextTurn(myMoves, enemies, depth+1)) < min) {
                            min = t_m;
                        }
                        //System.err.print(t_m + " " + nextMove + "\n");
                        enemy.remove(nextMove);
                    }
                    enemy.add(head);
                }

                return min;
            }
        }
    }
    private Point[] getNextMove(List<Point> moves)
    {
        return moves.get(moves.size()-1).getMoves();
    }
    private List<List<Point>> getEnemiesHeads()
    {
        List<List<Point>> enemies = new LinkedList<List<Point>>();
        for(int i = 0; i < this.n; i++) {
            //System.err.print(npcs[i]);
            if(!this.defeated(i)) {
                List<Point> enemy = new LinkedList<Point>();
                enemy.add(npcs[i].getHead());
                enemies.add(enemy);
            }
        }
        return enemies;
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
    //private MinMaxTree mmt;

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
        if (!npcs.defeated(id)) {
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

    private void defeat(int id) throws Exception
    {
        if(!npcs.defeated(id)) {
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

    public int score(List<Point> myMoves, List<List<Point>> enemies)
    {
        //System.err.print("Enemies " + enemies + "\n");
        //System.err.print("MyMoves " + myMoves + "\n");
        boolean[][] visited = new boolean[20][30];
        int     score = 0,
                sSize = 0;
        Queue<Point> myQueue = new LinkedList<Point>();
        Queue<Point> eQueue = new LinkedList<Point>();
        Point move = null;
        List<Point> enemy = null;
        List<Integer> dead = new LinkedList<Integer>();

        for(int i = 0; i < myMoves.size(); i++) {
            //make my move
            move = myMoves.get(i);
            if (!this.isBusy(move) && !visited[move.y][move.x]) {
                if ( i == myMoves.size() - 1 ) {
                    myQueue.add(move);
                } else {
                    visited[move.y][move.x] = true;
                }
            } else {
                return -10000;
            }

            //make enemies moves
            for (int j = 0; j < enemies.size(); j++) {
                if(dead.contains(i)){
                    continue;
                }
                enemy = enemies.get(j);
                if (i < enemy.size() ) {
                    move = enemy.get(i);

                    if (!this.isBusy(move) && !visited[move.y][move.x]) {
                        if (i == enemy.size() -1) {
                            eQueue.add(move);
                        } else {
                            visited[move.y][move.x] = true;
                        }
                    } else {
                        dead.add(j);
                        if (enemies.size() == dead.size()) {
                            return 10000;
                        }
                    }
                } else {
                    sSize = eQueue.size();
                    for (int k = 0; k < sSize; k++) {
                        move = eQueue.poll();
                        if (!this.isBusy(move) && !visited[move.y][move.x]) {
                            visited[move.y][move.x] = true;
                            score--;
                            for (Point pt : move.getMoves()) {
                                eQueue.add(pt);
                            }
                        }
                    }
                    break;
                }
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

    public int score2(Point meFirst, Point meSecond, List<Point> enemies, int p)
    {
        boolean[][] visited = new boolean[20][30];
        int score = 0,
                sSize = 0;
        Queue<Point> myQueue = new LinkedList<Point>();
        Queue<Point> eQueue = new LinkedList<Point>();
        Point point = null;

        for (Point e : enemies) {
            eQueue.add(e);
        }

        //Make First
        if (!this.isBusy(meFirst) && !visited[meFirst.y][meFirst.x]) {
            visited[meFirst.y][meFirst.x] = true;
            myQueue.add(meSecond);
        } else {
            return -1000;
        }
        while (!eQueue.isEmpty() || !myQueue.isEmpty()) {
            sSize = eQueue.size();
            for (int i = 0; i < sSize; i++) {
                point = eQueue.poll();
                if (!this.isBusy(point) && !visited[point.y][point.x]) {
                    visited[point.y][point.x] = true;
                    score--;
                    for (Point pt : point.getMoves()) {
                        eQueue.add(pt);
                    }
                }
            }

            sSize = myQueue.size();
            for (int i = 0; i < sSize; i++) {
                point = myQueue.poll();
                if (!this.isBusy(point) && !visited[point.y][point.x]) {
                    visited[point.y][point.x] = true;
                    score++;
                    for (Point pt : point.getMoves()) {
                        myQueue.add(pt);
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
