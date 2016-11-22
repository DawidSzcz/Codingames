import java.util.*;
import java.io.*;

class Player
{

    public static void main(String args[]) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in) );
        Board board = new Board();
        int N = 0, P;

        while (true) {
            if(! board.isInitialized()){
                StringTokenizer t = new StringTokenizer(reader.readLine());
                N = Integer.parseInt(t.nextToken());
                P = Integer.parseInt(t.nextToken());
                board.init(P, N);
            } else {
                reader.readLine();
            }

            for (int i = 0; i < N; i++) {
                board.insert(i, reader.readLine());
            }
            System.out.println(board.getMove());
        }
    }
}
