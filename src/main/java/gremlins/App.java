package gremlins;

import gremlins.gamemap.GameMap;
import java.io.*;
import java.util.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class App extends PApplet {
  /** =============== MAP SETTING =====================// */
  // The map consists of a grid of tiles 33 x 36
  public static final int MAP_ROW = 33;

  public static final int MAP_COLUMN = 36;
  // Each tile is 20x20 pixels,
  public static final int SPRITESIZE = 20;
  // The bottom 60 pixels of the window are reserved for the information bar
  public static final int BOTTOMBAR = 60;
  // The window size is 720x720.
  public static final int WIDTH = 720;
  public static final int HEIGHT = 720;
  /** =============== COLLISION SETTINGS =====================// */
  public static final Double ANTI_COLLISION_CONST = 1.1;

  public static final Double WIZARD_ANTI_COLLISION_CONST = 1.25;

  /** =============== KEYBOARD SETTINGS =====================// */
  public static final int LEFT = 37;

  public static final int RIGHT = 39;
  public static final int UP = 38;
  public static final int DOWN = 40;
  public static final int WIZARD_SHOOT = 32;
  /** =============== OTHER SETTINGS =====================// */
  public static final int FPS = 60;

  public static final Random randomGenerator = new Random();

  public String configPath;
  public Map<Character, PImage> imageMap;
  public Map<Character, List<PImage>> animationMap;
  public List<GameMap> gameMapList;
  public int wizardLives;
  public boolean mapHasExit;
  public boolean mapHasWizard;
  private GameSystem gameSystem;

  /** The config file is in located in config.json in the root directory of the project */
  public App() {
    this.configPath = "config.json";
  }

  public static void main(String[] args) {
    PApplet.main("gremlins.App");
  }

  /** Initialise the setting of the window size. */
  public void settings() {
    size(WIDTH, HEIGHT);
  }

  /**
   * Load all resources such as images. Initialise the elements such as the player, enemies and map
   * elements.
   */
  public void setup() {
    frameRate(FPS);
    imageMap = new HashMap<>();
    animationMap = new HashMap<>();
    gameMapList = new ArrayList<>();
    wizardLives = 0;
    doLoadImage();
    doloadGameMap();
    PlaySound.playBGM();
    gameSystem = new GameSystem(this, imageMap, animationMap, gameMapList, wizardLives);
  }

  /** Receive key pressed signal from the keyboard. */
  public void keyPressed() {
    gameSystem.keypress(keyCode);
    // System.out.println(keyCode);
  }

  /** Receive key released signal from the keyboard. */
  public void keyReleased() {}

  /** Draw all elements in the game by current frame. */
  public void draw() { // draw window
    fill(color(245, 245, 220)); // beige color reference from https://en.wikipedia.org/wiki/Beige
    this.rect(-1, -1, WIDTH + 1, HEIGHT + 1);
    gameSystem.refreshFrame();
  }

  private void doLoadImage() {
    // Load static objects
    imageMap.put(
        'X',
        loadImage(
            Objects.requireNonNull(this.getClass().getResource("stonewall.png"))
                .getPath()
                .replace("%20", " ")));
    imageMap.put(
        'B',
        loadImage(
            Objects.requireNonNull(this.getClass().getResource("brickwall.png"))
                .getPath()
                .replace("%20", " ")));
    imageMap.put(
        'F',
        loadImage(
            Objects.requireNonNull(this.getClass().getResource("fireball.png"))
                .getPath()
                .replace("%20", " ")));
    imageMap.put(
        'G',
        loadImage(
            Objects.requireNonNull(this.getClass().getResource("gremlin.png"))
                .getPath()
                .replace("%20", " ")));
    imageMap.put(
        'S',
        loadImage(
            Objects.requireNonNull(this.getClass().getResource("slime.png"))
                .getPath()
                .replace("%20", " ")));
    imageMap.put(
        'E',
        loadImage(
            Objects.requireNonNull(this.getClass().getResource("exit.png"))
                .getPath()
                .replace("%20", " ")));
    imageMap.put(
        'P',
        loadImage(
            Objects.requireNonNull(this.getClass().getResource("powerup.png"))
                .getPath()
                .replace("%20", " ")));
    imageMap.put(
        'U',
        loadImage(
            Objects.requireNonNull(this.getClass().getResource("supergremlin.png"))
                .getPath()
                .replace("%20", " ")));
    // Load dynamic objects
    // Store dynamic objects' name in one Array, then load all of them in a for loop
    String[] dynamicObjectNameArray = new String[] {"brickwall_destroyed", "wizard"};
    for (String dynamicObjectName : dynamicObjectNameArray) {
      List<PImage> imageList = new ArrayList<>();
      for (int j = 0; j < 4; j++) { // wizard and brickwall_destroyed all have 4 states. j < 4
        imageList.add(
            loadImage(
                (Objects.requireNonNull(this.getClass().getResource(dynamicObjectName + j + ".png"))
                    .getPath()
                    .replace("%20", " "))));
      }
      animationMap.put(dynamicObjectName.toUpperCase().charAt(0), imageList);
    }
  }

  private void doloadGameMap() {
    JSONObject config = loadJSONObject(new File(this.configPath));
    JSONArray gameStageArray = config.getJSONArray("levels");
    wizardLives = config.getInt("lives");

    for (int i = 0; i < gameStageArray.size(); i++) {
      JSONObject gameMapObject = gameStageArray.getJSONObject(i);
      String gameMapLayout = gameMapObject.getString("layout");
      double wizardAttackCooldown = gameMapObject.getDouble("wizard_cooldown");
      double enemyAttackCooldown = gameMapObject.getDouble("enemy_cooldown");

      try {
        Scanner mapDataScanner = new Scanner(new File(gameMapLayout));
        char[][] mapDataMatrix = new char[MAP_ROW][MAP_COLUMN];
        int currentPosition = 0;
        while (mapDataScanner.hasNextLine()) {
          mapDataMatrix[currentPosition] = mapDataScanner.nextLine().toCharArray();
          currentPosition++;
        }
        if (isValidateMap(mapDataMatrix)) {
          gameMapList.add(
              new GameMap(
                  gameMapLayout,
                  wizardAttackCooldown,
                  enemyAttackCooldown,
                  mapDataMatrix)); // can design by myself?
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private boolean isValidateMap(char[][] mapDataMatrix) throws Exception {
    // Create a dummy wizard to see if it can act normally in the map
    mapHasExit = false;
    mapHasWizard = false;
    int[] dummyWizardPosition = findWizard(mapDataMatrix);
    // Deep copy a map for test
    if (mapDataMatrix != null && dummyWizardPosition != null) {
      char[][] tempTestMap = new char[MAP_ROW][MAP_COLUMN];
      for (int i = 0; i < MAP_ROW; i++) {
        System.arraycopy(mapDataMatrix[i], 0, tempTestMap[i], 0, MAP_COLUMN);
      }
      // map is valid when 1. the map is OK 2. has a wizard (start point) 3. has an exit point
      boolean isValidateMap =
          checkMapValidity(tempTestMap, dummyWizardPosition[0], dummyWizardPosition[1])
              && mapHasExit
              && mapHasWizard;
      if (isValidateMap) return true;
    }
    throw new Exception("Error: Invalid Map Found.");
  }

  private int[] findWizard(char[][] map) {
    for (int row = 0; row < MAP_ROW; row++) {
      for (int col = 0; col < MAP_COLUMN; col++) {
        if (map[row][col] == 'W') {
          mapHasWizard = true;
          return new int[] {row, col};
        }
      }
    }
    return null;
  }

  private boolean checkMapValidity(char[][] map, int row, int col) {
    if (row < 0 || col < 0 || row > MAP_ROW - 1 || col > MAP_COLUMN - 1)
      return false; // corner cases
    if (map[row][col] == '*' || map[row][col] == 'X') {
      return true;
    }

    if (map[row][col] == 'E') mapHasExit = true;
    map[row][col] = '*'; // Mark accessible position as *
    // Use BFS to search.
    boolean left = checkMapValidity(map, row - 1, col);
    boolean right = checkMapValidity(map, row + 1, col);
    boolean up = checkMapValidity(map, row, col + 1);
    boolean down = checkMapValidity(map, row, col - 1);

    return left && right && up && down;
  }
}
