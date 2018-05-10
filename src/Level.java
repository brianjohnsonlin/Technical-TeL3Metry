import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Level {
    public final static int MAPTOIMAGESCALE = 32;
    public final static int SPACE_WHITE = 0;
    public final static int SPACE_BLACK = 1;
    public final static int SPACE_GRAY = 2;
    public final static int SPACE_INVALID = -1;

    protected Vector2 startingPoint;
    protected int[][] LevelMap;
    protected ArrayList<GameObjectData> gameObjectData;
    protected BackgroundImage bkgGray;
    protected BackgroundImage bkgGear;
    protected BackgroundImage bkgBlue;
    protected BackgroundImage bkgGreen;
    protected BackgroundImage bkgDigital;

    public Level() {
        gameObjectData = new ArrayList<>();
    }

    public void Load() {
        // create backgrounds
        bkgGray = new BackgroundImage("./res/bkg_gray.png", LevelMap, SPACE_GRAY);
        bkgGear = new BackgroundImage("./res/bkg_gear.png", LevelMap, SPACE_WHITE);
        bkgBlue = new BackgroundImage("./res/bkg_blue.png", LevelMap, SPACE_BLACK);
        bkgGreen = new BackgroundImage("./res/bkg_green.png", LevelMap, SPACE_WHITE);
        bkgDigital = new BackgroundImage("./res/bkg_digital.png", LevelMap, SPACE_BLACK);
        Game.instance.GameWindow.addImage(bkgGray);
        Game.instance.GameWindow.addImage(bkgGear);
        Game.instance.GameWindow.addImage(bkgBlue);
        Game.instance.GameWindow.addImage(bkgGreen);
        Game.instance.GameWindow.addImage(bkgDigital);
        bkgGreen.visible = bkgDigital.visible = false;

        // load all game objects
        for (GameObjectData data : gameObjectData) {
            Game.instance.AddGameObject(new GameObject(data));
        }

        // place player at starting position
        Game.instance.GetPlayer().position.replaceWith(startingPoint);
    }

    protected void ImportLevelMap(String filename) {
        try {
            BufferedImage levelMapBI = ImageIO.read(new File(filename));
            int width = levelMapBI.getWidth();
            int height = levelMapBI.getHeight();
            LevelMap = new int[height][width];
            int[] level_map = levelMapBI.getRGB(0, 0, width, height, null, 0, width);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = level_map[y * width + x];
                    if (pixel == 0xFFFFFFFF) {        // WHITE
                        LevelMap[y][x] = SPACE_WHITE;
                    } else if (pixel == 0xFF000000) { // BLACK
                        LevelMap[y][x] = SPACE_BLACK;
                    } else {                          // GRAY and everything else
                        LevelMap[y][x] = SPACE_GRAY;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UnLoad() {
        Game.instance.GameWindow.clearLayer(0); // Delete Background
        Game.instance.DestroyAllGameObjects();
    }

    public int GetSpaceType(Vector2 coord) {
        // out of bounds
        if (coord.x < 0 || coord.x >= LevelMap[0].length*MAPTOIMAGESCALE || coord.y < 0 || coord.y >= LevelMap.length*MAPTOIMAGESCALE) {
            return SPACE_INVALID;
        }

        return LevelMap[(int)coord.y/MAPTOIMAGESCALE][(int)coord.x/MAPTOIMAGESCALE];
    }

    public void Invert(boolean inverted) {
        bkgDigital.visible = inverted;
        bkgGreen.visible = inverted;
        bkgGear.visible = !inverted;
        bkgBlue.visible = !inverted;
    }
}
