/**
 * Created by kazik on 27.11.16.
 */
class Snuffle extends Obj
{
    boolean taken;

    public Snuffle(int id, int x, int y, int vx, int vy) {
        super(id, x, y, vx, vy);
        this.taken = false;
    }

    public void take()
    {
        this.taken = true;
    }

    public boolean isAviable()
    {
        return !taken;
    }

    protected String getType() {
        return "SNUFFLE ";
    }
}
