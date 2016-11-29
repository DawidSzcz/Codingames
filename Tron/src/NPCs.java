import java.util.LinkedList;
import java.util.List;

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
