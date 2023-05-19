package gremlins.dynamicobjects;

import gremlins.gamemap.*;
import gremlins.staticobjects.*;

import java.util.*;

public class Slime extends Projectile {
    int x;
    int y;
    int direction;
    private final int SLIME_MOVE_SPEED = 4;
    public Slime(int x, int y, GameMap gameMap, int direction) {
        super(x, y, gameMap, direction);
    }

    public boolean canSlimeMove() {
        int x = getX();
        int y = getY();
        int slimeDirection = getDirection();
        List<StaticObjects> slimeList = new ArrayList<>();
        slimeList.addAll(getGameMap().getSolidWallList());
        slimeList.addAll(getGameMap().getBrickWallList());

        return !projectileMove(slimeDirection, slimeList, SLIME_MOVE_SPEED);
    }
}
