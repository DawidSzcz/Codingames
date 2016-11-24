import java.util.LinkedList;
import java.util.List;

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
