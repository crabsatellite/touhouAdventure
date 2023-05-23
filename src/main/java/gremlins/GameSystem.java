package gremlins;

///////////////////// OK start //////////////////////////////////////////////

import static gremlins.App.*;

import gremlins.dynamicobjects.*;
import gremlins.gamemap.GameMap;
import gremlins.staticobjects.BrickWall;
import gremlins.staticobjects.StaticObjects;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import processing.core.PApplet;
import processing.core.PImage;

public class GameSystem {
    public Map<Character, PImage> imageMap;
    public Map<Character, List<PImage>> animationMap;
    public List<GameMap> gameMapList;
    public int stage;
    public int wizardLives;
    public int initialPowerUpCoolDown = 600;
    public int powerUpRefreshTimeRemain = 600;
    public int powerUpRow;
    public int powerUpCol;
    public int powerUpDuration = 600;
    public int buffTime = 0;

    public PApplet pApplet;
    public GameMap gameMap;

    public GameSystem(PApplet pApplet, Map<Character, PImage> imageMap, Map<Character, List<PImage>> animationMap, List<GameMap> gameMapList, int wizardLives) { //unfinished

        this.pApplet = pApplet;
        this.imageMap = imageMap;
        this.animationMap = animationMap;
        this.gameMapList = gameMapList;
        this.wizardLives = wizardLives;
        this.stage = 0;
        this.gameMap = gameMapList.get(stage);
        generatePowerUpItem();
    }

    public void refreshFrame() {
        if (stage < gameMapList.size()) {
            if (wizardLives != 0) { // In game
                drawStaticItems();
                drawAnimation();
                drawInformationBar();
                drawPowerUp();
                checkIfCharacterDead();
                checkIfStageClear();
                checkIfWizardGetPowerUp();
            } else {
                // draw gameover until r pressed
                if (pApplet.keyPressed && pApplet.key == 'r') {
                    restartGame();
                } else {
                    drawGameOver();
                }
            }
        } else {
            if (pApplet.keyPressed && pApplet.key == 'r') {
                restartGame();
            } else {
                drawYouWin();
            }
        }
    }

    private void restartGame() {
        wizardLives = 3;
        stage = 0;
        gameMap = gameMapList.get(stage);
        gameMap.reset();
        generatePowerUpItem();
    }

    private void checkIfWizardGetPowerUp() {
        if (gameMap.getWizard().getPowerUp(powerUpRow * SPRITESIZE, powerUpCol * SPRITESIZE)) {
            PlaySound.playPowerUpSound();
            buffTime = powerUpDuration;
            powerUpRow = -10 * SPRITESIZE; // Let power up out of the screen
            powerUpCol = -10 * SPRITESIZE;
        } else {
            if (buffTime != 0) {
                buffTime--;
            }
        }
    }

    private void generatePowerUpItem() {
        powerUpRow = randomGenerator.nextInt() % MAP_ROW;
        powerUpCol = randomGenerator.nextInt() % MAP_COLUMN;
        while (powerUpRow < 0 || powerUpCol < 0 || powerUpCol >= MAP_COLUMN || powerUpRow >= MAP_ROW || gameMap.getMapDataMatrix()[powerUpRow][powerUpCol] != ' ') {
            powerUpRow = randomGenerator.nextInt() % MAP_ROW;
            powerUpCol = randomGenerator.nextInt() % MAP_COLUMN;
        }
    }

    private void drawPowerUp() {
        if (powerUpRefreshTimeRemain == 0) {
            generatePowerUpItem();
            powerUpRefreshTimeRemain = initialPowerUpCoolDown;
        } else {
            pApplet.image(imageMap.get('P'), powerUpCol * SPRITESIZE, powerUpRow * SPRITESIZE);
            powerUpRefreshTimeRemain--;
        }
    }

    private void checkIfStageClear() {
        if (gameMap.getWizard().stageClear()) {
            stage++;
            PlaySound.playStageClearSound();
            if (stage < gameMapList.size()) {
                gameMap = gameMapList.get(stage);
                gameMap.getWizard().setWizardAttackCooldown(0);
                powerUpRefreshTimeRemain = 0;
                buffTime = 0;
                gameMap.reset();
            }
        }
    }

    private void drawGameOver() {
        pApplet.textSize(30);
        pApplet.fill(0, 0, 0);
        pApplet.text("GAME OVER", 12 * SPRITESIZE, 19 * SPRITESIZE);
        pApplet.textSize(20);
        pApplet.fill(0, 0, 0);
        pApplet.text("Press R to retry", 13 * SPRITESIZE, 21 * SPRITESIZE);
    }

    private void drawYouWin() {
        pApplet.textSize(30);
        pApplet.fill(0, 0, 0);
        pApplet.text("YOU WIN", 14 * SPRITESIZE, 19 * SPRITESIZE);
        pApplet.textSize(20);
        pApplet.fill(0, 0, 0);
        pApplet.text("Press R to retry", 14 * SPRITESIZE, 21 * SPRITESIZE);
    }

    private void drawInformationBar() {
        pApplet.textSize(20);
        pApplet.fill(0, 0, 0);
        pApplet.text("Lives", 5, 34 * SPRITESIZE + 15);

        int x = 34 * SPRITESIZE;
        int y = 3 * SPRITESIZE;
        for (int i = 0; i < wizardLives; i++) {
            pApplet.image(animationMap.get('W').get(1), y, x);
            y += SPRITESIZE;
        }

        pApplet.textSize(20);
        pApplet.fill(0, 0, 0);
        pApplet.text("level " + (stage + 1) + "/" + gameMapList.size(), 10 * SPRITESIZE, 34 * SPRITESIZE + 8);

        if (buffTime != 0) {
            pApplet.textSize(20);
            pApplet.fill(0, 0, 0);
            pApplet.text("POWER UP TIME REMAIN: " + buffTime / 60 + "s", 10 * SPRITESIZE, 34 * SPRITESIZE + 30);
        }

        if (gameMap.getWizard().getWizardAttackCooldown() != 0) {
            double gameMapCooldown = Math.ceil(gameMap.getWizardAttackCooldown() * FPS);
            if (gameMapCooldown != 0) {
                int length = 60;
                pApplet.fill(0, 0, 0);
                pApplet.rect(30 * SPRITESIZE, 34 * SPRITESIZE, length, 6);
                int black = (int) (gameMap.getWizard().getWizardAttackCooldown() / gameMapCooldown * length);
                pApplet.fill(0, 0, 0);
                pApplet.rect(30 * SPRITESIZE, 34 * SPRITESIZE, length - black, 6);
            }
        }
    }

    private void checkIfCharacterDead() {
        //check if gremlins dead
        List<Gremlins> gremlinsList = gameMap.getGremlinsList();
        Wizard wizard = gameMap.getWizard();
        List<Fireball> fireballList = wizard.getFireballList();
        Iterator<Gremlins> gremlinsIterator = gremlinsList.iterator();
        while (gremlinsIterator.hasNext()) {
            Gremlins gremlins = gremlinsIterator.next();
            if (gremlins.hitByFireball(fireballList)) {
                gremlins.gremlinsRespawn(wizard.getAnimationObjectPosX(), wizard.getAnimationObjectPosY());
            }
            //check if slime dead
            gremlins.slimeVapourised(fireballList);
        }

        //check if supergremlins dead
        List<SuperGremlins> superGremlinsList = gameMap.getSuperGremlinsList();
        Iterator<SuperGremlins> superGremlinsIterator = superGremlinsList.iterator();
        while (superGremlinsIterator.hasNext()) {
            SuperGremlins superGremlins = superGremlinsIterator.next();
            if (superGremlins.hitByFireball(fireballList)) {
                superGremlins.superGremlinsRespawn(wizard.getAnimationObjectPosX(), wizard.getAnimationObjectPosY());
            }
            //check if superslime dead
//            superGremlins.superSlimeVapourised(fireballList);
        }

        //check if wizard dead
        if (wizard.isDead(gremlinsList, superGremlinsList)) {
            wizardLives--;
            PlaySound.playWizardDeadSound();
            gameMap.reset();
            //wizard.setWizardAttackCooldown(0);
        }
    }

    private void drawStaticItems() {
        //draw solid wall
        List<StaticObjects> solidWallListList = gameMap.getSolidWallList();
        for (StaticObjects solidWall : solidWallListList) {
            pApplet.image(imageMap.get('X'), solidWall.getCol() * SPRITESIZE, solidWall.getRow() * SPRITESIZE);
        }

        //draw brickwall
        List<BrickWall> brickWallList = gameMap.getBrickWallList();
        Iterator<BrickWall> brickWallIterator = brickWallList.iterator();
        while (brickWallIterator.hasNext()) {
            BrickWall brickWall = brickWallIterator.next();
            int brickWallState = brickWall.getBrickWallState();
            if (brickWallState == -1) {
                pApplet.image(imageMap.get('B'), brickWall.getCol() * SPRITESIZE, brickWall.getRow() * SPRITESIZE);
            } else {
                if (brickWallState < 4) {
                    pApplet.image(animationMap.get('B').get(brickWallState), brickWall.getCol() * SPRITESIZE, brickWall.getRow() * SPRITESIZE);//unfinished?
                } else {
                    brickWallIterator.remove();
                }
            }
            brickWall.updateBrickWallState();
        }

        //draw exit
        StaticObjects exit = gameMap.getExit();
        pApplet.image(imageMap.get('E'), exit.getCol() * SPRITESIZE, exit.getRow() * SPRITESIZE);
    }

    private void drawAnimation() {
        //draw wizard
        Wizard wizard = gameMap.getWizard();
        wizard.wizardTrueMove();
        PImage wizardImage = null;
        if (wizard.getDirection() == App.LEFT) {
            wizardImage = animationMap.get('W').get(0);
        } else if (wizard.getDirection() == App.RIGHT) {
            wizardImage = animationMap.get('W').get(1);
        } else if (wizard.getDirection() == App.UP) {
            wizardImage = animationMap.get('W').get(2);
        } else if (wizard.getDirection() == App.DOWN) {
            wizardImage = animationMap.get('W').get(3);
        }
        if (wizardImage != null) {
            pApplet.image(wizardImage, wizard.getAnimationObjectPosY(), wizard.getAnimationObjectPosX());
        }
        wizard.updateWizardAttackCoolDown();

        //draw fireball
        List<Fireball> fireballList = wizard.getFireballList();
        Iterator<Fireball> fireballIterator = fireballList.iterator();

        while (fireballIterator.hasNext()) {
            Fireball fireball = fireballIterator.next();

            pApplet.image(imageMap.get('F'), fireball.getY(), fireball.getX());
            if (!fireball.fireballMove()) {
                fireballIterator.remove();
            }
        }
        //draw supergremlins and slimes
        List<SuperGremlins> superGremlinsList = gameMap.getSuperGremlinsList();
        Iterator<SuperGremlins> superGremlinsIterator = superGremlinsList.iterator();
        while (superGremlinsIterator.hasNext()) {
            SuperGremlins superGremlins = superGremlinsIterator.next();
            pApplet.image(imageMap.get('U'), superGremlins.getAnimationObjectPosY(), superGremlins.getAnimationObjectPosX());
            if (buffTime == 0) {
                superGremlins.superGremlinsMove();
            }
        }
        //draw gremlins and slimes
        List<Gremlins> gremlinsList = gameMap.getGremlinsList();
        Iterator<Gremlins> gremlinsIterator = gremlinsList.iterator();
        while (gremlinsIterator.hasNext()) {
            Gremlins gremlins = gremlinsIterator.next();
            pApplet.image(imageMap.get('G'), gremlins.getAnimationObjectPosY(), gremlins.getAnimationObjectPosX());
            if (buffTime == 0) {
                gremlins.gremlinsMove();
            }
            List<Slime> slimeList = gremlins.getSlimeList();
            Iterator<Slime> slimeIterator = slimeList.iterator();
            while (slimeIterator.hasNext()) {
                Slime slime = slimeIterator.next();
                pApplet.image(imageMap.get('S'), slime.getY(), slime.getX());
                if (slime.canSlimeMove()) {
                    slimeIterator.remove();
                }
            }
        }
    }

    public void keypress(int keyCode) {
        gameMap.getWizard().wizardMove(keyCode);
    }

}