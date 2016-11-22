import java.util.LinkedList;
import java.util.List;

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
    public void makeMove(Point p)
    {
        me.makeMove(p);
    }
    public String getMove()
    {
        return me.getMove();
    }
}
