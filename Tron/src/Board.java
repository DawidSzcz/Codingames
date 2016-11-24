import java.util.*;

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
