import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by kazik on 27.11.16.
 */
class Context
{
    public static int myTeamId;
    public static Point enemyBase;
    public static Point myBase;
    public static Point mid = new Point(8000, 0);
    public static List<Point> throwOrder = new LinkedList<>();
    public static Scanner in;
    public static int throwRadius = 300;
    public static int defenderArea = 9000;

    public Context(int myTeamId) {
        this.myTeamId = myTeamId;
        if(myTeamId == 0) {
            enemyBase = new Point(16000, 3750);
            myBase = new Point(0, 3750);
            throwOrder.add(enemyBase);
            throwOrder.add(new Point(16000, 0));
            throwOrder.add(new Point(16000, 7500));
            throwOrder.add(new Point(12000, 0));
            throwOrder.add(new Point(12000, 7500));
            throwOrder.add(new Point(8000, 0));
            throwOrder.add(new Point(8000, 7500));
            throwOrder.add(new Point(4000, 0));
            throwOrder.add(new Point(4000, 7500));
            throwOrder.add(new Point(0, 0));
            throwOrder.add(new Point(0, 7500));
        } else {
            enemyBase = new Point(0, 3750);
            myBase = new Point(16000, 3750);
            throwOrder.add(enemyBase);
            throwOrder.add(new Point(0, 0));
            throwOrder.add(new Point(0, 7500));
            throwOrder.add(new Point(4000, 0));
            throwOrder.add(new Point(4000, 7500));
            throwOrder.add(new Point(8000, 0));
            throwOrder.add(new Point(8000, 7500));
            throwOrder.add(new Point(8000, 0));
            throwOrder.add(new Point(8000, 7500));
            throwOrder.add(new Point(16000, 0));
            throwOrder.add(new Point(16000, 7500));
            throwOrder.add(new Point(12000, 0));
            throwOrder.add(new Point(12000, 7500));
        }
    }
}
