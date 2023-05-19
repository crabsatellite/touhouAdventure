package gremlins.dynamicobjects;

import gremlins.App;
import gremlins.gamemap.*;
import gremlins.staticobjects.*;

import java.util.*;
import static gremlins.App.*;

public class Gremlins extends Animation {
    public static int GREMLINS_MOVE_SPEED = 1;
    private List<Slime> slimeList;
    private int gremlinsAttackCooldown;
    private HashSet<Integer> previousDirection;

    public Gremlins (int x, int y, GameMap gameMap) {
        super(x, y, gameMap);

        setDirection(randomGenerator.nextInt() % 4 + App.LEFT);
        previousDirection = new HashSet<>();
        slimeList = new ArrayList<>();
        previousDirection.add(getDirection());
        this.gremlinsAttackCooldown = (int) gameMap.getEnemyAttackCooldown() * FPS;//this?
    }

    public List<Slime> getSlimeList() {
        return slimeList;
    }

    public void gremlinsMove() {
        List<StaticObjects> wallList = new ArrayList<>();
        wallList.addAll(getGameMap().getSolidWallList());
        wallList.addAll(getGameMap().getBrickWallList());
        int gremlinsDirection = getDirection();
        int gremlinsX = getAnimationObjectPosX();
        int gremlinsY = getAnimationObjectPosY();
        if (gremlinsDirection == App.UP && entityCanWalk(wallList, gremlinsX - GREMLINS_MOVE_SPEED, gremlinsY)) {
            super.setAnimationObjectPosX(gremlinsX - GREMLINS_MOVE_SPEED);
            super.setDirection(App.UP);
        } else if (gremlinsDirection == App.LEFT && entityCanWalk(wallList, gremlinsX, gremlinsY - GREMLINS_MOVE_SPEED)) {
            super.setAnimationObjectPosY(gremlinsY - GREMLINS_MOVE_SPEED);
            super.setDirection(App.LEFT);
        } else if (gremlinsDirection == App.DOWN && entityCanWalk(wallList, gremlinsX + GREMLINS_MOVE_SPEED, gremlinsY)) {
            super.setAnimationObjectPosX(gremlinsX +  GREMLINS_MOVE_SPEED);
            super.setDirection(App.DOWN);
        } else if (gremlinsDirection == App.RIGHT && entityCanWalk(wallList, gremlinsX, gremlinsY + GREMLINS_MOVE_SPEED)) {
            super.setAnimationObjectPosY(gremlinsY + GREMLINS_MOVE_SPEED);
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

        if (gremlinsAttackCooldown == 0) {
            gremlinsAttackCooldown = (int) getGameMap().getEnemyAttackCooldown() * FPS;
            slimeList.add(new Slime(gremlinsX, gremlinsY, getGameMap(), gremlinsDirection));
        } else {
            gremlinsAttackCooldown--;
        }
    }

    public boolean hitByFireball(List<Fireball> fireballList) {
        Iterator<Fireball> fireballIterator = fireballList.iterator();
        int x = getAnimationObjectPosX();
        int y = getAnimationObjectPosY();

        while (fireballIterator.hasNext()) {
            Fireball fireball = fireballIterator.next();
            int fireballX = fireball.getX();
            int fireballY = fireball.getY();
            if ((x >= fireballX - SPRITESIZE / ANTI_COLLISION_CONST) && (x <= (fireballX + SPRITESIZE / ANTI_COLLISION_CONST)) && (y >= fireballY - SPRITESIZE / ANTI_COLLISION_CONST) && y <= (fireballY + SPRITESIZE / ANTI_COLLISION_CONST)) {
                fireballIterator.remove();
                return true;
            }
        }
        return false;
    }

    public void slimeVapourised (List<Fireball> fireballList) {
        Iterator<Fireball> fireballIterator = fireballList.iterator();
        while (fireballIterator.hasNext()) {
            Fireball fireball = fireballIterator.next();
            int fireballX = fireball.getX();
            int fireballY = fireball.getY();
            Iterator<Slime> slimeIterator = getSlimeList().iterator();
            while (slimeIterator.hasNext()) {
                Slime slime = slimeIterator.next();
                int slimeX = slime.getX();
                int slimeY = slime.getY();
                if ((slimeX >= fireballX - SPRITESIZE / ANTI_COLLISION_CONST) && (slimeX <= (fireballX + SPRITESIZE / ANTI_COLLISION_CONST)) && (slimeY >= fireballY - SPRITESIZE / ANTI_COLLISION_CONST) && slimeY <= (fireballY + SPRITESIZE / ANTI_COLLISION_CONST)) {
                    slimeIterator.remove();
                    fireballIterator.remove();
                }
            }
        }
    }

    public void gremlinsRespawn(int wizardX, int wizardY) {
        int respawnPositionX = randomGenerator.nextInt() % MAP_ROW;
        int respawnPositionY = randomGenerator.nextInt() % MAP_COLUMN;
        while (Math.sqrt(Math.pow(wizardX - respawnPositionX, 2) + Math.pow(wizardY - respawnPositionY, 2))  <= 10 || (respawnPositionX == getAnimationObjectPosX() && respawnPositionY == getAnimationObjectPosY()) || respawnPositionX < 0 || respawnPositionY < 0 || getGameMap().getMapDataMatrix()[respawnPositionX][respawnPositionY] != ' ') {
            respawnPositionX = randomGenerator.nextInt() % MAP_ROW;
            respawnPositionY = randomGenerator.nextInt() % MAP_COLUMN;
        }
        setAnimationObjectPosX(respawnPositionX * SPRITESIZE);
        setAnimationObjectPosY(respawnPositionY * SPRITESIZE);
    }
}