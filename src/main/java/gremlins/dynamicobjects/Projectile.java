package gremlins.dynamicobjects;

import static gremlins.App.*;

import gremlins.App;
import gremlins.gamemap.GameMap;
import gremlins.staticobjects.StaticObjects;
import java.util.List;

public abstract class Projectile {
    private final GameMap gameMap;
    private int x;
    private int y;
    private int direction;

    public Projectile(int x, int y, GameMap gameMap, int direction) {
        this.x = x;
        this.y = y;
        this.gameMap = gameMap;
        this.direction = direction;

    }

    public int getX () {return x;}

    public void setX(int x) {
        this.x = x;
    }

    public int getY () {return y;}

    public void setY(int y) {
        this.y = y;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public boolean canWalkInThisDirection(List<StaticObjects> staticObjectsList, int x, int y) {
        for (StaticObjects staticObjects : staticObjectsList) {
            int itemX = staticObjects.getRow() * SPRITESIZE;
            int itemY = staticObjects.getCol() * SPRITESIZE;
            if ((x >= itemX - SPRITESIZE / ANTI_COLLISION_CONST) && (x <= (itemX + SPRITESIZE / ANTI_COLLISION_CONST)) && (y >= itemY - SPRITESIZE / ANTI_COLLISION_CONST) && y <= (itemY + SPRITESIZE / ANTI_COLLISION_CONST)) {
                return false;
            }
        }
        return true;
    }

    public boolean projectileMove(int direction, List<StaticObjects> staticObjectsList, int speed) {
        int x = getX();
        int y = getY();
        if (direction == App.UP && canWalkInThisDirection(staticObjectsList, x - speed, y)) {
            setX(x - speed);
            setDirection(App.UP);
            return true;
        } else if (direction == App.DOWN && canWalkInThisDirection(staticObjectsList, x + speed, y)) {
            setX(x + speed);
            setDirection(App.DOWN);
            return true;
        } else if (direction == App.RIGHT && canWalkInThisDirection(staticObjectsList, x, y + speed)) {
            setY(y + speed);
            setDirection(App.RIGHT);
            return true;
        } else if (direction == App.LEFT && canWalkInThisDirection(staticObjectsList, x, y - speed)) {
            setY(y - speed);
            setDirection(App.LEFT);
            return true;
        }
        return false;
    }
}