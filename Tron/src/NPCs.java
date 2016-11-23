import java.util.Arrays;
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
            while (id < this.n - 1) {
                npcs[id] = npcs[++id];
            }
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
    public NPC[] getQueue()
    {
        NPC[] ids = new NPC[this.n];
        for(int i = 0; i < this.n; i++) {
            ids[i] = npcs[i];
        }
        return ids;
    }
    public int playersCount()
    {
        return this.n;
    }
    public String makeMove()
    {
        /*Point[][] Possibilies = new Point[this.n][];
        for(int i = 0; i < 0; i++) {
            Possibilies[i] = npcs[i].getPossibilities();
        }

        for(int i = 0; i < 0; i++) {
            Possibilies[i] = npcs[i].getPossibilities();
        }*/

        Point[] possibs = me.getPossibilities();
        List<Point> enemiesPos = getEnemiesPossibs();
        int max = -1000000, t_m;
        Point best = null;
        for(Point possib : possibs){
            if((t_m =board.score(possib, enemiesPos, this.p)) > max) {
                max = t_m;
                best = possib;
            }
            System.err.println(t_m + ": " + possib);
        }
        return me.makeMove(best);
    }
    private List<Point> getEnemiesPossibs()
    {
        List<Point> enemiesPos = new LinkedList<Point>();
        int k = 0;
        for(int i = 0; i < this.n; i++) {
            if (i == this.p) {

            } else {
                enemiesPos.addAll(Arrays.asList(npcs[i].getPossibilities()));
            }
        }
        return enemiesPos;
    }
}
