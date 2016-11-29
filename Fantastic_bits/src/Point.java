/**
 * Created by kazik on 27.11.16.
 */
class Point
{
    int x, y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public double dist(Point p)
    {
        return Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
    }

    public double distFromLine(Point B, Point E)
    {
        System.err.println("FROM: " + B.toString() + " TO: " + E.toString());
        double normalLength = Math.sqrt((B.x-E.x)*(B.x-E.x)+(B.y-E.y)*(B.y-E.y));
        double dist = Math.abs((this.x-E.x)*(B.y-E.y)-(this.y-E.y)*(B.x-E.x))/normalLength;
        System.err.println(this.toString() + " DIST: " + dist);
        return dist;
    }

    public String toString()
    {
        return this.x + " " + this.y;
    }
}
