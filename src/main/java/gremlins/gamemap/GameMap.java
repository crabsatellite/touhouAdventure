package gremlins.gamemap;

import static gremlins.App.*;

import gremlins.dynamicobjects.*;
import gremlins.staticobjects.*;
import java.util.*;

public class GameMap {
  private final String gameMapLayout;
  private final double wizardAttackCooldown;
  private final double enemyAttackCooldown;
  private char[][] mapDataMatrix;
  private Wizard wizard;
  private List<Gremlins> gremlinsList;
  private List<SuperGremlins> superGremlinsList;
  private List<BrickWall> brickWallList;
  private List<StaticObjects> solidWallList;

  private List<Fireball> fireballList;
  private List<Slime> slimeList;
  // private List<Items> exitList;
  private StaticObjects exit;

  public GameMap(
      String gameMapLayout,
      double wizardAttackCooldown,
      double enemyAttackCooldown,
      char[][] mapDataMatrix) {
    this.gameMapLayout = gameMapLayout;
    this.wizardAttackCooldown = wizardAttackCooldown;
    this.enemyAttackCooldown = enemyAttackCooldown;
    this.mapDataMatrix = mapDataMatrix;
    this.superGremlinsList = new ArrayList<>();
    this.gremlinsList = new ArrayList<>();
    this.brickWallList = new ArrayList<>();
    this.solidWallList = new ArrayList<>();
    this.fireballList = new ArrayList<>();
    this.slimeList = new ArrayList<>();
    readGameData();
  }

  public List<SuperGremlins> getSuperGremlinsList() {
    return superGremlinsList;
  }

  public String getGameMapLayout() {
    return gameMapLayout;
  }

  public double getWizardAttackCooldown() {
    return wizardAttackCooldown;
  }

  public double getEnemyAttackCooldown() {
    return enemyAttackCooldown;
  }

  public char[][] getMapDataMatrix() {
    return mapDataMatrix;
  }

  public Wizard getWizard() {
    return wizard;
  }

  public List<BrickWall> getBrickWallList() {
    return brickWallList;
  }

  public List<Fireball> getFireballList() {
    return fireballList;
  }

  public List<Gremlins> getGremlinsList() {
    return gremlinsList;
  }

  public List<StaticObjects> getSolidWallList() {
    return solidWallList;
  }

  //    public List<Items> getExitList() {
  //        return exitList;
  //    }

  public StaticObjects getExit() {
    return exit;
  }

  public void reset() {
    gremlinsList.clear();
    superGremlinsList.clear();
    brickWallList.clear();
    solidWallList.clear();
    slimeList.clear();
    fireballList.clear();
    wizard.setWizardAttackCooldown(0);
    wizard = null;
    exit = null;
    readGameData();
  }

  private void readGameData() {
    for (int row = 0; row < MAP_ROW; row++) {
      for (int col = 0; col < MAP_COLUMN; col++) {
        if (mapDataMatrix[row][col] == 'W') wizard = new Wizard(row, col, this);
        else if (mapDataMatrix[row][col] == 'G') gremlinsList.add(new Gremlins(row, col, this));
        else if (mapDataMatrix[row][col] == 'U')
          superGremlinsList.add(new SuperGremlins(row, col, this));
        else if (mapDataMatrix[row][col] == 'B') brickWallList.add(new BrickWall(row, col));
        else if (mapDataMatrix[row][col] == 'X') solidWallList.add(new SolidWall(row, col));
        else if (mapDataMatrix[row][col] == 'E') exit = new Exit(row, col);
      }
    }
  }
}
