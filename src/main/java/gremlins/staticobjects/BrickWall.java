package gremlins.staticobjects;

public class BrickWall extends StaticObjects {
    private int brickWallState;
    private int row;
    private int col;

    public BrickWall (int row, int col) {
        super(row, col);
        this.brickWallState = -1;
    }


    public int getBrickWallState() {
        return brickWallState;
    }

    public void setBrickWallState(int brickWallState) {
        this.brickWallState = brickWallState;
    }

    public void updateBrickWallState() {
        if (brickWallState != -1) {
            brickWallState++;
        }
    }
}
