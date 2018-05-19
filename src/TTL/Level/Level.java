package TTL.Level;

import TTL.*;
import TTL.GameObject.*;
import TTL.GameObject.Device.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Level {
    public final static int SPACE_WHITE = 0;
    public final static int SPACE_BLACK = 1;
    public final static int SPACE_GRAY = 2;
    public final static int SPACE_INVALID = -1;

    protected Vector2 startingPoint;
    protected boolean startInverted;
    protected int[][] LevelMap;
    protected ArrayList<GameObjectData> gameObjectData;

    public Level() {
        gameObjectData = new ArrayList<>();
        startInverted = false;
    }

    public void Load() {
        // load all game objects
        for (GameObjectData data : gameObjectData) {
            if (data.DeviceType == GameObject.DEVICENONE) {
                Game.instance.AddGameObject(new GameObject(data));
            } else if (data.DeviceType == GameObject.DEVICEGATE) {
                Game.instance.AddGameObject(new Gate(data));
            } else if (data.DeviceType == GameObject.DEVICEBUTTON) {
                Game.instance.AddGameObject(new Button(data));
            } else if (data.DeviceType == GameObject.DEVICESTONE) {
                Game.instance.AddGameObject(new Stone(data));
            } else if (data.DeviceType == GameObject.DEVICEFORCEFIELD) {
                Game.instance.AddGameObject(new Forcefield(data));
            } else if (data.DeviceType == GameObject.DEVICETYPINGTEXT) {
                Game.instance.AddGameObject(new TypingText(data));
            }
        }

        Game.instance.GetPlayer().Invert(startInverted);                // set player inversion
        Game.instance.GetPlayer().Position.replaceWith(startingPoint);  // place player at starting Position
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

    public int[][] GetLevelMap() {
        return LevelMap;
    }

    public int GetSpaceType(Vector2 coord) {
        // out of bounds
        if (coord.x < 0 || coord.x >= Game.instance.GameWindow.GetWidth() || coord.y < 0 || coord.y >= Game.instance.GameWindow.GetHeight()) {
            return SPACE_INVALID;
        }

        return LevelMap[(int)(coord.y / Game.instance.GameWindow.GetHeight() * LevelMap.length)]
                       [(int)(coord.x / Game.instance.GameWindow.GetWidth() * LevelMap[0].length)];
    }

    public Vector2 GetStartingPoint() {
        return startingPoint;
    }

    public void Unload() {
        Game.instance.DestroyAllGameObjects();
    }
}
