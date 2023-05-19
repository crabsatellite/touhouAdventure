package gremlins.dynamicobjects;

import static gremlins.App.*;

import gremlins.App;
import gremlins.PlaySound;
import gremlins.gamemap.*;
import gremlins.staticobjects.*;
import java.util.*;

public class Wizard extends Animation {
  public static int FRAME_SPEED = 4;
  public static int MOVE_SPEED = 20;
  private List<Fireball> fireballList;
  private int wizardAttackCooldown;
  private int moveDircetion;
  private int moveStep = 0;

  public Wizard(int x, int y, GameMap gameMap) {
    super(x, y, gameMap);
    fireballList = new ArrayList<>();
    this.wizardAttackCooldown = (int) gameMap.getWizardAttackCooldown() * FPS;
  }

  public static int getMoveSpeed() {
    return MOVE_SPEED;
  }

  public int getWizardAttackCooldown() {
    return wizardAttackCooldown;
  }

  public void setWizardAttackCooldown(int cooldown) {
    this.wizardAttackCooldown = cooldown;
  }

  public void wizardTrueMove() {
    if (moveStep > 5 || moveStep <= 0) {
      moveStep = 0;
      return;
    }

    int x = super.getAnimationObjectPosX();
    int y = super.getAnimationObjectPosY();
    if (moveDircetion == UP) {
      super.setAnimationObjectPosX(x - FRAME_SPEED);
    } else if (moveDircetion == LEFT) {
      super.setAnimationObjectPosY(y - FRAME_SPEED);
    } else if (moveDircetion == RIGHT) {
      super.setAnimationObjectPosY(y + FRAME_SPEED);
    } else if (moveDircetion == DOWN) {
      super.setAnimationObjectPosX(x + FRAME_SPEED);
    }
    moveStep += 1;
  }

  public void wizardMove(int keyCode) {
    if (moveStep != 0) {
      return;
    }

    int x = super.getAnimationObjectPosX();
    int y = super.getAnimationObjectPosY();
    List<StaticObjects> wallList = new ArrayList<>();
    wallList.addAll(getGameMap().getSolidWallList());
    wallList.addAll(getGameMap().getBrickWallList());
    if (keyCode == App.UP) {
      setDirection(App.UP);
      if (wizardCanWalk(wallList, x - MOVE_SPEED, y)) {
        moveDircetion = App.UP;
        moveStep = 1;
      }

    } else if (keyCode == App.LEFT) {
      super.setDirection(App.LEFT);
      if (wizardCanWalk(wallList, x, y - MOVE_SPEED)) {
        moveDircetion = App.LEFT;
        moveStep = 1;
      }
    } else if (keyCode == App.RIGHT) {
      super.setDirection(App.RIGHT);
      if (wizardCanWalk(wallList, x, y + MOVE_SPEED)) {
        moveDircetion = App.RIGHT;
        moveStep = 1;
      }
    } else if (keyCode == App.DOWN) {

      super.setDirection(App.DOWN);
      if (wizardCanWalk(wallList, x + MOVE_SPEED, y)) {
        moveDircetion = App.DOWN;
        moveStep = 1;
      }
    }

    if (keyCode == WIZARD_SHOOT) {
      if (wizardAttackCooldown <= 0) {
        PlaySound.playFireballShootSound();
        fireballList.add(new Fireball(x, y, getGameMap(), getDirection()));
        wizardAttackCooldown = (int) Math.ceil(super.getGameMap().getWizardAttackCooldown() * FPS);
      }
    }
  }

  public void updateWizardAttackCoolDown() {
    if (wizardAttackCooldown > 0) {
      wizardAttackCooldown--;
    }
  }

  public List<Fireball> getFireballList() {
    return fireballList;
  }

  public boolean isDead(List<Gremlins> gremlinsList, List<SuperGremlins> superGremlinsList) {
    int x = getAnimationObjectPosX();
    int y = getAnimationObjectPosY();
    for (Gremlins gremlins : gremlinsList) {
      int gremlinX = gremlins.getAnimationObjectPosX();
      int gremlinY = gremlins.getAnimationObjectPosY();
      if ((x >= gremlinX - SPRITESIZE / ANTI_COLLISION_CONST)
          && (x <= (gremlinX + SPRITESIZE / ANTI_COLLISION_CONST))
          && (y >= gremlinY - SPRITESIZE / ANTI_COLLISION_CONST)
          && y <= (gremlinY + SPRITESIZE / ANTI_COLLISION_CONST)) {
        return true;
      }
      List<Slime> slimeList = gremlins.getSlimeList();
      Iterator<Slime> slimeIterator = slimeList.iterator();
      while (slimeIterator.hasNext()) {
        Slime slime = slimeIterator.next();
        int slimeX = slime.getX();
        int slimeY = slime.getY();
        if ((x >= slimeX - SPRITESIZE / ANTI_COLLISION_CONST)
            && (x <= (slimeX + SPRITESIZE / ANTI_COLLISION_CONST))
            && (y >= slimeY - SPRITESIZE / ANTI_COLLISION_CONST)
            && y <= (slimeY + SPRITESIZE / ANTI_COLLISION_CONST)) {
          slimeIterator.remove();
          return true;
        }
      }
    }

    for (SuperGremlins superGremlins : superGremlinsList) {
      int superGremlinX = superGremlins.getAnimationObjectPosX();
      int superGremlinY = superGremlins.getAnimationObjectPosY();
      if ((x >= superGremlinX - SPRITESIZE / ANTI_COLLISION_CONST)
          && (x <= (superGremlinX + SPRITESIZE / ANTI_COLLISION_CONST))
          && (y >= superGremlinY - SPRITESIZE / ANTI_COLLISION_CONST)
          && y <= (superGremlinY + SPRITESIZE / ANTI_COLLISION_CONST)) {
        return true;
      }
    }

    return false;
  }

  public boolean stageClear() {
    int x = getAnimationObjectPosX();
    int y = getAnimationObjectPosY();
    int exitX = getGameMap().getExit().getRow() * SPRITESIZE;
    int exitY = getGameMap().getExit().getCol() * SPRITESIZE;

    return ((x >= exitX - SPRITESIZE / ANTI_COLLISION_CONST)
        && (x <= (exitX + SPRITESIZE / ANTI_COLLISION_CONST))
        && (y >= exitY - SPRITESIZE / ANTI_COLLISION_CONST)
        && y <= (exitY + SPRITESIZE / ANTI_COLLISION_CONST));
  }

  public boolean getPowerUp(int powerupX, int powerupY) {
    int x = getAnimationObjectPosX();
    int y = getAnimationObjectPosY();

    return ((x >= powerupX - SPRITESIZE / ANTI_COLLISION_CONST
            && x <= powerupX + SPRITESIZE / ANTI_COLLISION_CONST))
        && ((y >= powerupY - SPRITESIZE / ANTI_COLLISION_CONST
            && y <= powerupY + SPRITESIZE / ANTI_COLLISION_CONST));
  }
}
