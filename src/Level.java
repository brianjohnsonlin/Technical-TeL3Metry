import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Level {
    protected Vector2 startingPoint;
    protected int[][] LevelMap;
    protected ArrayList<GameObjectData> gameObjectData;

    public Level() {
        gameObjectData = new ArrayList<>();
    }

    public void Load() {
        // create backgrounds
        Game.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_gray.png", LevelMap, 2));
        Game.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_gear.png", LevelMap, 0));
        Game.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_blue.png", LevelMap, 1));
        //Game.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_green.png", LevelMap, 0), 0);
        //Game.instance.GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_digital.png", LevelMap, 1), 0);

        // load all game objects
        for (GameObjectData data : gameObjectData) {
            Game.instance.AddGameObject(new GameObject(data));
        }

        // place player at starting position
        Game.instance.GetPlayer().position.replaceWith(startingPoint);
    }

    public void UnLoad() {
        // delete background images
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
                    } else {                                             // GRAY and everything else
                        LevelMap[y][x] = 2;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}