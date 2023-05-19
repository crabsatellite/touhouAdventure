package gremlins.dynamicobjects;

import static gremlins.App.*;

import gremlins.App;
import gremlins.gamemap.*;
import gremlins.staticobjects.*;
import java.util.*;

public class Fireball extends Projectile {

  public Fireball(int x, int y, GameMap gameMap, int direction) {
    super(x, y, gameMap, direction);
  }

  public boolean fireballMove() {
    int fireballX = getX();
    int fireballY = getY();
    int fireballDirection = getDirection();
    List<StaticObjects> staticObjectsList = new ArrayList<>();
    staticObjectsList.addAll(getGameMap().getSolidWallList());
    staticObjectsList.addAll(getGameMap().getBrickWallList());

    int FIREBALL_MOVE_SPEED = 4;
    if (!projectileMove(fireballDirection, staticObjectsList, FIREBALL_MOVE_SPEED)) {
      if (fireballDirection == App.UP) {
        fireballX -= FIREBALL_MOVE_SPEED;
      } else if (fireballDirection == App.DOWN) {
        fireballX += FIREBALL_MOVE_SPEED;
      } else if (fireballDirection == App.LEFT) {
        fireballY -= FIREBALL_MOVE_SPEED;
      } else if (fireballDirection == App.RIGHT) {
        fireballY += FIREBALL_MOVE_SPEED;
      }

      List<BrickWall> brickWallList = getGameMap().getBrickWallList();
      for (BrickWall brickWall : brickWallList) {
        int brickWallX = brickWall.getRow() * SPRITESIZE;
        int brickWallY = brickWall.getCol() * SPRITESIZE;
        if ((fireballX >= brickWallX - SPRITESIZE / ANTI_COLLISION_CONST)
            && (fireballX <= (brickWallX + SPRITESIZE / ANTI_COLLISION_CONST))
            && (fireballY >= brickWallY - SPRITESIZE / ANTI_COLLISION_CONST)
            && fireballY <= (brickWallY + SPRITESIZE / ANTI_COLLISION_CONST)) {
          brickWall.setBrickWallState(0);
        }
      }
      return false;
    }
    return true;
  }
}
