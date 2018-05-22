package TTL.Level;

import TTL.*;
import TTL.GameObject.*;

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
    protected int[][] levelMap;
    protected ArrayList<GameObjectData> gameObjectData;

    public Level() {
        gameObjectData = new ArrayList<>();
        startInverted = false;
    }

    public void Load() {
        // load all game objects
        for (GameObjectData data : gameObjectData) {
            switch (data.Type) {
                case GameObject.DEVICEGATE:
                    Game.instance.AddGameObject(new Gate(data));
                    break;
                case GameObject.DEVICEBUTTON:
                    Game.instance.AddGameObject(new Button(data));
                    break;
                case GameObject.DEVICESTONE:
                    Game.instance.AddGameObject(new Stone(data));
                    break;
                case GameObject.DEVICETYPINGTEXT:
                    Game.instance.AddGameObject(new TypingText(data));
                    break;
                case GameObject.FORCEFIELD:
                    Game.instance.AddGameObject(new Forcefield(data));
                    break;
                case GameObject.DEVICEHINT:
                    Game.instance.AddGameObject(new Hint(data));
                    break;
                case GameObject.DEVICEMOVEHINT:
                    Game.instance.AddGameObject(new MoveHint(data));
                    break;
                default:
                    Game.instance.AddGameObject(new GameObject(data));
                    break;
            }
        }

        Game.instance.GetPlayer().Invert(startInverted);                // set player inversion
        Game.instance.SetBackgroundsInverted(startInverted);
        Game.instance.GetPlayer().Position.replaceWith(startingPoint);  // place player at starting Position
    }

    protected void ImportLevelMap(String filename) {
        try {
            BufferedImage levelMapBI = ImageIO.read(new File(filename));
            int width = levelMapBI.getWidth();
            int height = levelMapBI.getHeight();
            levelMap = new int[height][width];
            int[] level_map = levelMapBI.getRGB(0, 0, width, height, null, 0, width);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    switch (level_map[y * width + x]) {
                        case 0xFFFFFFFF: // WHITE
                            levelMap[y][x] = SPACE_WHITE;
                            break;
                        case 0xFF000000: // BLACK
                            levelMap[y][x] = SPACE_BLACK;
                            break;
                        default:         // GRAY and everything else
                            levelMap[y][x] = SPACE_GRAY;
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[][] GetLevelMap() {
        return levelMap;
    }

    public Vector2 GetStartingPoint() {
        return startingPoint;
    }

    public boolean GetStartInverted() {
        return startInverted;
    }
}
