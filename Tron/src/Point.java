class Point
{
    public int x,  y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public int manDist(Point p)
    {
        return Math.abs(this.x - p.x) + Math.abs(this.y - p.y);
    }

    public Point[] getMoves()
    {
        Point[] points = {new Point(x + 1, y), new Point(x - 1, y), new Point(x, y + 1),new Point(x, y - 1)};
        return points;
    }
    public boolean equals(Point p)
    {
        return this.x == p.x && this.y == p.y;
    }

    public Point[] getMoves(String current)
    {
        Point[] pairs = {new Point(x + 1, y), new Point(x - 1, y), new Point(x, y + 1),new Point(x, y - 1)};
        Point temp;
        switch(current){
            case "LEFT":
                temp = pairs[1];
                pairs[1] = pairs[0];
                pairs[0] = temp;
                break;
            case "DOWN":
                temp = pairs[2];
                pairs[2] = pairs[0];
                pairs[0] = temp;
                break;
            case "UP":
                temp = pairs[3];
                pairs[3] = pairs[0];
                pairs[0] = temp;
                break;
        }
        //System.err.println(pairs.toString());
        return pairs;
    }
    public String toString()
    {
        return "POINT: " + x + " " + y;
    }
}
