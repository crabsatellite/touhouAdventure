package gremlins.dynamicobjects;
import gremlins.App;
import gremlins.gamemap.GameMap;
import gremlins.staticobjects.StaticObjects;

import static gremlins.App.*;
import java.util.*;

public abstract class Animation {
    private int animationObjectPosX;
    private int animationObjectPosY;
    private int direction;
    private GameMap gameMap;
    public Animation(int x, int y, GameMap gameMap) {
        this.animationObjectPosX = x * SPRITESIZE;
        this.animationObjectPosY = y * SPRITESIZE;
        this.direction = App.RIGHT;
        this.gameMap = gameMap;
    }

    public int getAnimationObjectPosX() {
        return animationObjectPosX;
    }

    public int getAnimationObjectPosY() {
        return animationObjectPosY;
    }

    public int getDirection() {
        return direction;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setAnimationObjectPosY(int animationObjectPosY) {
        this.animationObjectPosY = animationObjectPosY;
    }
    public void setAnimationObjectPosX(int animationObjectPosX) {
        this.animationObjectPosX = animationObjectPosX;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public boolean entityCanWalk(List<StaticObjects> staticObjectsList, int entityX, int entityY) {
        for (StaticObjects staticObjects : staticObjectsList) {
            int staticObjectX = staticObjects.getRow() * SPRITESIZE;
            int staticObjectY = staticObjects.getCol() * SPRITESIZE;
            if ((entityX >= staticObjectX - SPRITESIZE / ANTI_COLLISION_CONST) && (entityX <= (staticObjectX + SPRITESIZE / ANTI_COLLISION_CONST)) && (entityY >= staticObjectY - SPRITESIZE / ANTI_COLLISION_CONST) &&  entityY <= (staticObjectY + SPRITESIZE / ANTI_COLLISION_CONST)) {
                return false;
            }
        }
        return true;
    }
    public boolean wizardCanWalk(List<StaticObjects> wallList, int wizardX, int wizardY) {
        for (StaticObjects wall : wallList) {
            int wallX = wall.getRow() * SPRITESIZE;
            int wallY = wall.getCol() * SPRITESIZE;
            if ((wizardX >= wallX - SPRITESIZE / WIZARD_ANTI_COLLISION_CONST) && (wizardX <= (wallX + SPRITESIZE / WIZARD_ANTI_COLLISION_CONST)) && (wizardY >= wallY - SPRITESIZE / WIZARD_ANTI_COLLISION_CONST) &&  wizardY <= (wallY + SPRITESIZE / WIZARD_ANTI_COLLISION_CONST)) {
                return false;
            }
        }
        return true;
    }
}
