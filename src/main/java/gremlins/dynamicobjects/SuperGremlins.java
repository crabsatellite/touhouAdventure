package gremlins.dynamicobjects;

import static gremlins.App.*;

import gremlins.App;
import gremlins.gamemap.GameMap;
import gremlins.staticobjects.StaticObjects;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class SuperGremlins extends Animation {
  public static int SUPER_GREMLINS_MOVE_SPEED = 3;
  private int superGremlinsAttackCooldown;
  private HashSet<Integer> previousDirection;

  public SuperGremlins(int x, int y, GameMap gameMap) {
    super(x, y, gameMap);

    setDirection(randomGenerator.nextInt() % 4 + App.LEFT);
    previousDirection = new HashSet<>();
    previousDirection.add(getDirection());
    this.superGremlinsAttackCooldown =
        (int) gameMap.getEnemyAttackCooldown()
            * FPS
            / 2; // Double the rate of fire compared to gremlins
  }

  public void superGremlinsMove() {
    List<StaticObjects> wallList = new ArrayList<>();
    wallList.addAll(getGameMap().getSolidWallList());
    wallList.addAll(getGameMap().getBrickWallList());
    int superGremlinsDirection = getDirection();
    int superGremlinsX = getAnimationObjectPosX();
    int superGremlinsY = getAnimationObjectPosY();
    if (superGremlinsDirection == App.UP
        && entityCanWalk(wallList, superGremlinsX - SUPER_GREMLINS_MOVE_SPEED, superGremlinsY)) {
      super.setAnimationObjectPosX(superGremlinsX - SUPER_GREMLINS_MOVE_SPEED);
      super.setDirection(App.UP);
    } else if (superGremlinsDirection == App.LEFT
        && entityCanWalk(wallList, superGremlinsX, superGremlinsY - SUPER_GREMLINS_MOVE_SPEED)) {
      super.setAnimationObjectPosY(superGremlinsY - SUPER_GREMLINS_MOVE_SPEED);
      super.setDirection(App.LEFT);
    } else if (superGremlinsDirection == App.DOWN
        && entityCanWalk(wallList, superGremlinsX + SUPER_GREMLINS_MOVE_SPEED, superGremlinsY)) {
      super.setAnimationObjectPosX(superGremlinsX + SUPER_GREMLINS_MOVE_SPEED);
      super.setDirection(App.DOWN);
    } else if (superGremlinsDirection == App.RIGHT
        && entityCanWalk(wallList, superGremlinsX, superGremlinsY + SUPER_GREMLINS_MOVE_SPEED)) {
      super.setAnimationObjectPosY(superGremlinsY + SUPER_GREMLINS_MOVE_SPEED);
      super.setDirection(App.RIGHT);
    } else {
      int currentGeneratedDirection = randomGenerator.nextInt() % 4 + App.LEFT;
      while (previousDirection.contains(currentGeneratedDirection)) {
        currentGeneratedDirection = randomGenerator.nextInt() % 4 + App.LEFT;
      }
      previousDirection.add(currentGeneratedDirection);
      if (previousDirection.size() == 4) { // All direction has been generated.
        previousDirection.clear();
      }
      setDirection(currentGeneratedDirection);
    }

    //        if (superGremlinsAttackCooldown == 0) {
    //            superGremlinsAttackCooldown = (int) getGameMap().getEnemyAttackCooldown() * FPS;
    // // Double the attack rate
    //            superSlimeList.add(new SuperSlime(superGremlinsX, superGremlinsY, getGameMap(),
    // superGremlinsDirection));
    //        } else {
    //            superGremlinsAttackCooldown--;
    //        }
  }

  public boolean hitByFireball(List<Fireball> fireballList) {
    Iterator<Fireball> fireballIterator = fireballList.iterator();
    int x = getAnimationObjectPosX();
    int y = getAnimationObjectPosY();

    while (fireballIterator.hasNext()) {
      Fireball fireball = fireballIterator.next();
      int fireballX = fireball.getX();
      int fireballY = fireball.getY();
      if ((x >= fireballX - SPRITESIZE / ANTI_COLLISION_CONST)
          && (x <= (fireballX + SPRITESIZE / ANTI_COLLISION_CONST))
          && (y >= fireballY - SPRITESIZE / ANTI_COLLISION_CONST)
          && y <= (fireballY + SPRITESIZE / ANTI_COLLISION_CONST)) {
        fireballIterator.remove();
        return true;
      }
    }
    return false;
  }

  public void superGremlinsRespawn(int wizardX, int wizardY) {
    int respawnPositionX = randomGenerator.nextInt() % MAP_ROW;
    int respawnPositionY = randomGenerator.nextInt() % MAP_COLUMN;
    while (Math.sqrt(
                Math.pow(wizardX - respawnPositionX, 2) + Math.pow(wizardY - respawnPositionY, 2))
            <= 10
        || (respawnPositionX == getAnimationObjectPosX()
            && respawnPositionY == getAnimationObjectPosY())
        || respawnPositionX < 0
        || respawnPositionY < 0
        || getGameMap().getMapDataMatrix()[respawnPositionX][respawnPositionY] != ' ') {
      respawnPositionX = randomGenerator.nextInt() % MAP_ROW;
      respawnPositionY = randomGenerator.nextInt() % MAP_COLUMN;
    }
    setAnimationObjectPosX(respawnPositionX * SPRITESIZE);
    setAnimationObjectPosY(respawnPositionY * SPRITESIZE);
  }
}
