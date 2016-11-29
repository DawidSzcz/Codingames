import java.util.Collection;
import java.math.*;
import java.util.Arrays;
import java.util.HashMap;
import java.io.*;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;
import java.util.*;

/**
 * Created by kazik on 27.11.16.
 */
class Context
{
    public static int myTeamId;
    public static Point enemyBase;
    public static Point myBase;
    public static Point mid = new Point(8000, 0);
    public static List<Point> throwOrder = new LinkedList<>();
    public static Scanner in;
    public static int throwRadius = 300;
    public static int defenderArea = 9000;

    public Context(int myTeamId) {
        this.myTeamId = myTeamId;
        if(myTeamId == 0) {
            enemyBase = new Point(16000, 3750);
            myBase = new Point(0, 3750);
            throwOrder.add(enemyBase);
            throwOrder.add(new Point(16000, 0));
            throwOrder.add(new Point(16000, 7500));
            throwOrder.add(new Point(12000, 0));
            throwOrder.add(new Point(12000, 7500));
            throwOrder.add(new Point(8000, 0));
            throwOrder.add(new Point(8000, 7500));
            throwOrder.add(new Point(4000, 0));
            throwOrder.add(new Point(4000, 7500));
            throwOrder.add(new Point(0, 0));
            throwOrder.add(new Point(0, 7500));
        } else {
            enemyBase = new Point(0, 3750);
            myBase = new Point(16000, 3750);
            throwOrder.add(enemyBase);
            throwOrder.add(new Point(0, 0));
            throwOrder.add(new Point(0, 7500));
            throwOrder.add(new Point(4000, 0));
            throwOrder.add(new Point(4000, 7500));
            throwOrder.add(new Point(8000, 0));
            throwOrder.add(new Point(8000, 7500));
            throwOrder.add(new Point(8000, 0));
            throwOrder.add(new Point(8000, 7500));
            throwOrder.add(new Point(16000, 0));
            throwOrder.add(new Point(16000, 7500));
            throwOrder.add(new Point(12000, 0));
            throwOrder.add(new Point(12000, 7500));
        }
    }
}

/**
 * Created by kazik on 28.11.16.
 */
class Snuffles
{
    private List<Snuffle> snufs = null;

}

/**
 * Grab Snaffles and try to throw them through the opponent's goal!
 * Move towards a Snaffle and use your team id to determine where you need to throw it.
 **/
class Player {

    public static void main(String args[]) {
        Context.in = new Scanner(System.in);
        Context c= new Context(Context.in.nextInt()); // if 0 you need to score on the right of the map, if 1 you need to score on the left
        Board board = new Board();
        // game loop
        while (true) {
            int entities = Context.in.nextInt(); // number of entities still in game
            for (int i = 0; i < entities; i++) {
                board.insert();
            }
            board.makeMove();
        }
    }
}
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

/**
 * Created by kazik on 27.11.16.
 */
class NPCs
{
    private Defender defender = new Defender();
    private Striker striker = new Striker();
    private List<Point> enemies = new LinkedList<>();
    private int strikerId =-1;

    public void addEnemy(int x, int y, int vx, int vz)
    {
        this.enemies.add(new Point(x, y));
    }
    public void makeMove(List<Snuffle> snufs)
    {
        striker.makeMove(snufs, enemies);
        defender.makeMove(snufs, enemies);
        enemies.clear();
    }

    public void update(int id, int x, int y, int vx, int vy, int state) {
        if(strikerId == -1 || strikerId == id) {
            striker.update(x, y, vx, vy, state);
            strikerId = id;
        } else {
            defender.update(x, y, vx, vy, state);
        }
    }
}
/**
 * Created by kazik on 27.11.16.
 */
class Snuffle extends Obj
{
    boolean taken;

    public Snuffle(int id, int x, int y, int vx, int vy) {
        super(id, x, y, vx, vy);
        this.taken = false;
    }

    public void take()
    {
        this.taken = true;
    }

    public boolean isAviable()
    {
        return !taken;
    }

    protected String getType() {
        return "SNUFFLE ";
    }
}

/**
 * Created by kazik on 27.11.16.
 */
class Striker extends Ally
{
    protected void moveToClosest(List<Snuffle> snufs)
    {
        System.err.println(snufs);
        double max = 1000000, t_m;
        Snuffle goal = null;
        for(Snuffle snuf : snufs) {
            if(snuf.isAviable() && (t_m = this.dist(snuf)) < max) {
                max = t_m;
                goal = snuf;
            }
        }
        if(goal != null) {
            goal.take();
            System.out.println("MOVE " + goal.pos.toString() + " 150");
        } else {
            System.out.println("MOVE " + Context.myBase.toString() + " 150");
        }
    }

    protected String getType() {
        return "STRIKER ";
    }
}
/**
 * Created by kazik on 27.11.16.
 */
abstract class Obj
{
    int id;
    int vx;
    int vy;
    public Point pos;

    protected abstract String getType();

    public Obj()
    {
    }
    public Obj(int id, int x, int y, int vx, int vy)
    {
        this.id = id;
        this.vx = vx;
        this.vy = vy;
        this.pos = new Point(x, y);
    }

    public void update(int x, int y, int vx, int vy)
    {
        this.pos = new Point(x, y);
        this.vx = vx;
        this.vy = vy;
    }

    public double dist(Obj o)
    {
        return this.pos.dist(o.pos);
    }

    public int getId()
    {
        return this.id;
    }

    public String toString()
    {
        return this.getType() + this.pos.toString() + "\n";
    }
}

/**
 * Created by kazik on 29.11.16.
 */
abstract class Ally extends Obj
{
    int state;

    protected abstract void moveToClosest(List<Snuffle> snufs);

    public void makeMove(List<Snuffle> snufs, List<Point> enemies)
    {
        if(this.isCurring()) {
            System.out.println("THROW " + this.bestThrow(enemies).toString() + " 500");
        } else {
            this.moveToClosest(snufs);
        }
    }
    public void update(int x, int y, int vx, int vy, int state)
    {
        super.update(x, y, vx, vy);
        System.err.println(this.getType());
        this.state = state;
    }

    protected boolean isCurring()
    {
        return this.state == 1;
    }

    protected Point bestThrow(List<Point> enemies)
    {
        double max = 100000, t_m;
        Point best = Context.enemyBase;
        for(Point goal: Context.throwOrder) {
            for(Point enemy: enemies) {
                if(enemies.get(0).distFromLine(this.pos, goal) > Context.throwRadius &&
                   enemies.get(1).distFromLine(this.pos, goal) > Context.throwRadius) {
                    if((t_m = Context.enemyBase.dist(goal)) < max) {
                        max = t_m;
                        best = goal;
                    }
                }
            }
        }
        return best;
    }
}

/**
 * Created by kazik on 27.11.16.
 */
class Board
{
    private List<Snuffle> snufs = new LinkedList<>();
    private NPCs allies= new NPCs();

    public void insert(){

        int entityId = Context.in.nextInt(); // entity identifier
        String entityType = Context.in.next(); // "WIZARD", "OPPONENT_WIZARD" or "SNAFFLE" (or "BLUDGER" after first league)
        int x       = Context.in.nextInt();
        int y       = Context.in.nextInt();
        int vx      = Context.in.nextInt();
        int vy      = Context.in.nextInt();
        int state   = Context.in.nextInt();
        switch(entityType) {
            case "WIZARD":
                allies.update(entityId, x, y, vx, vy, state);
                break;
            case "SNAFFLE":
                snufs.add(new Snuffle(entityId, x, y, vx, vy));
                break;
            case "BLUDGER":
            case "OPPONENT_WIZARD":
                allies.addEnemy(x, y, vx, vy);
            default:
                System.err.println("dfault");
        }
    }
    public void makeMove()
    {
        allies.makeMove(snufs);
        snufs.clear();
    }
}
/**
 * Created by kazik on 27.11.16.
 */
class Point
{
    int x, y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public double dist(Point p)
    {
        return Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
    }

    public double distFromLine(Point B, Point E)
    {
        System.err.println("FROM: " + B.toString() + " TO: " + E.toString());
        double normalLength = Math.sqrt((B.x-E.x)*(B.x-E.x)+(B.y-E.y)*(B.y-E.y));
        double dist = Math.abs((this.x-E.x)*(B.y-E.y)-(this.y-E.y)*(B.x-E.x))/normalLength;
        System.err.println(this.toString() + " DIST: " + dist);
        return dist;
    }

    public String toString()
    {
        return this.x + " " + this.y;
    }
}
