import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Level {
    protected Vector2 startingPoint;
    protected int[][] LevelMap;
    protected ArrayList<GameObject> gameObjects;

    public Level() {}

    public void Load() {
        // create backgrounds
        TTL.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_gray.png", LevelMap, 2), 0);
        TTL.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_gear.png", LevelMap, 0), 0);
        TTL.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_blue.png", LevelMap, 1), 0);
        //TTL.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_green.png", LevelMap, 0), 0);
        //TTL.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_digital.png", LevelMap, 1), 0);

        // load all game objects
        for (GameObject g : gameObjects) {
            TTL.instance.GameWindow.addImage(g.GetSprite(), 1);
        }

        // place player at starting position
    }

    public void UnLoad() {
        // delete all GameObjects
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
                    if (level_map[y * width + x] == 0xFFFFFFFF) {        // WHITE
                        LevelMap[y][x] = 0;
                    } else if (level_map[y * width + x] == 0xFF000000) { // BLACK
                        LevelMap[y][x] = 1;
                    } else {                                             // GRAY
                        LevelMap[y][x] = 2;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}