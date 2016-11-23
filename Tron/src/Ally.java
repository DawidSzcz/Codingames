class Ally extends NPC
{

    private String currentMove = "RIGHT";

    public Ally(int id, Board board)
    {
        super(id, board);
    }

    protected String getType()
    {
        return "ALLY";
    }

    public Point[] getPossibilities()
    {
        return body.peek().getMoves(this.currentMove);
    }

    public String makeMove(Point p)
    {
        Point myPos = body.peek();
        if (myPos.x < p.x) {
            this.currentMove = "RIGHT";
        }
        if (myPos.x > p.x) {
            this.currentMove = "LEFT";
        }
        if (myPos.y > p.y) {
            this.currentMove = "UP";
        }
        if (myPos.y < p.y) {
            this.currentMove = "DOWN";
        }
        return this.currentMove;
    }

    public String getMove()
    {
        return this.currentMove;
    }
}
