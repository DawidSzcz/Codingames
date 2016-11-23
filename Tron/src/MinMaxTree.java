import java.util.HashMap;
import java.util.Map;

/**
 * Created by kazik on 22.11.16.
 */
class MinMaxTree
{
    private NPCs players;
    private Board board;
    private Move root;
    private int me;
    private int steps;

    public MinMaxTree(NPCs npcs, Board board, int p, int depth)
    {
        this.root       = new Move(board);
        this.me         = p;
        this.board      = board;
        this.players    = npcs;
        this.steps      = depth;
    }

    public void makeMove()
    {
        NPC[] queue = players.getQueue();
        Move root = new Move(this.board);
        this.makeMove(root, queue, 0);

        this.makeMove(this.findBest(root));
    }

    public String getMove()
    {
        return null;
    }

    private void makeMove(Move move)
    {
        while(move.previous != null)
        {
            if(move.id != this.me) {
                move = move.previous;
            }
        }
        players.makeMove();
    }
    public void makeMove(Move move, NPC[] queue, int depth)
    {
        System.err.print("MMF: " + move);
        if(depth >= queue.length * this.steps){

        } else {
            //queue[depth%queue.length].makeMove(move, depth);
            for(Move nextMove : move.nextMoves) {
                this.makeMove(nextMove, queue, depth + 1);
            }
        }
    }
    public Move findBest(Move move)
    {
        if(move.nextMoves == null) {
            move.scoreTurn(this.getLastTurn(move), this.me);
            return move;
        } else {
            Move best = null;
            for(Move nextMove : move.nextMoves){
                Move t_b = findBest(nextMove);
                if ((best == null || t_b.score > best.score)) {
                    best = t_b;
                }
            }
            return best;
        }
    }
    private Move[] getLastTurn(Move move)
    {
        Move[] ms= null;
        int n= players.playersCount();
        if(n == 2){
            ms = new Move[]{move.previous, move};
        }
        if(n == 3){
            ms = new Move[]{move.previous, move.previous, move};
        }
        if(n == 4){
            ms = new Move[]{move.previous, move.previous, move.previous, move};
        }
        return ms;
    }

}
