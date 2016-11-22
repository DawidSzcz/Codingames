import java.util.StringTokenizer;

class Board {
    int[][] board = new int[20][30];
    private NPCs npcs = null;
    private MinMaxTree mmt;

    public boolean isInitialized()
    {
        return  npcs != null;
    }

    public void init(int P, int N)
    {
        npcs = new NPCs(P, N, this);
        mmt = new MinMaxTree(npcs, this, P, 2);
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
        mmt.makeMove();
        return mmt.getMove();
    }

    public boolean isBusy(Point p)
    {
        return !(p.x >= 0 && p.x < 30 && p.y >= 0 && p.y < 20 && board[p.y][p.x] == 0);
    }

}
