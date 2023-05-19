package gremlins.dynamicobjects;

import gremlins.gamemap.*;
import gremlins.staticobjects.*;
import java.util.*;

public class Slime extends Projectile {
  private final int SLIME_MOVE_SPEED = 4;
  int x;
  int y;
  int direction;

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
