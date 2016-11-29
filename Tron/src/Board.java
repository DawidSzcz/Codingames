import java.util.*;

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
