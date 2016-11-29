/**
 * Created by kazik on 27.11.16.
 */
abstract class Obj
{
    int id;
    int vx;
    int vy;
    public Point pos;

    protected abstract String getType();

    public Obj()
    {
    }
    public Obj(int id, int x, int y, int vx, int vy)
    {
        this.id = id;
        this.vx = vx;
        this.vy = vy;
        this.pos = new Point(x, y);
    }

    public void update(int x, int y, int vx, int vy)
    {
        this.pos = new Point(x, y);
        this.vx = vx;
        this.vy = vy;
    }

    public double dist(Obj o)
    {
        return this.pos.dist(o.pos);
    }

    public int getId()
    {
        return this.id;
    }

    public String toString()
    {
        return this.getType() + this.pos.toString() + "\n";
    }
}
