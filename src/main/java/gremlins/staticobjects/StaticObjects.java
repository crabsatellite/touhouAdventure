package gremlins.staticobjects;

public abstract class StaticObjects {
    private int row;
    private int col;
    public StaticObjects(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
