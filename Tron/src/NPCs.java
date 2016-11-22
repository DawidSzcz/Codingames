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
