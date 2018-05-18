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
    public final static int MAPTOIMAGESCALE = 32;
    public final static int SPACE_WHITE = 0;
    public final static int SPACE_BLACK = 1;
    public final static int SPACE_GRAY = 2;
    public final static int SPACE_INVALID = -1;

    protected Vector2 startingPoint;
    protected boolean startInverted;
    protected int[][] LevelMap;
    protected ArrayList<GameObjectData> gameObjectData;
    protected BackgroundImage bkgGray;
    protected BackgroundImage bkgGear;
    protected BackgroundImage bkgBlue;
    protected BackgroundImage bkgGreen;
    protected BackgroundImage bkgDigital;

    public Level() {
        gameObjectData = new ArrayList<>();
        startInverted = false;
    }

    public void Load() {
        // create backgrounds
        bkgGray = new BackgroundImage("./res/bkg_gray.png", LevelMap, SPACE_GRAY);
        bkgGear = new BackgroundImage("./res/bkg_gear.png", LevelMap, SPACE_WHITE);
        bkgBlue = new BackgroundImage("./res/bkg_blue.png", LevelMap, SPACE_BLACK);
        bkgGreen = new BackgroundImage("./res/bkg_green.png", LevelMap, SPACE_WHITE);
        bkgDigital = new BackgroundImage("./res/bkg_digital.png", LevelMap, SPACE_BLACK);
        Game.instance.GameWindow.addSprite(bkgGray);
        Game.instance.GameWindow.addSprite(bkgGear);
        Game.instance.GameWindow.addSprite(bkgBlue);
        Game.instance.GameWindow.addSprite(bkgGreen);
        Game.instance.GameWindow.addSprite(bkgDigital);
        bkgGreen.Visible = bkgDigital.Visible = startInverted;
        bkgBlue.Visible = bkgGear.Visible = !startInverted;

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

    public int GetSpaceType(Vector2 coord) {
        // out of bounds
        if (coord.x < 0 || coord.x >= LevelMap[0].length*MAPTOIMAGESCALE || coord.y < 0 || coord.y >= LevelMap.length*MAPTOIMAGESCALE) {
            return SPACE_INVALID;
        }

        return LevelMap[(int)coord.y/MAPTOIMAGESCALE][(int)coord.x/MAPTOIMAGESCALE];
    }

    public void Invert(boolean inverted) {
        bkgDigital.Visible = inverted;
        bkgGreen.Visible = inverted;
        bkgGear.Visible = !inverted;
        bkgBlue.Visible = !inverted;
    }

    public Vector2 GetStartingPoint() {
        return startingPoint;
    }

    public void Unload() {
        Game.instance.GameWindow.removeSprite(bkgGray);
        Game.instance.GameWindow.removeSprite(bkgGear);
        Game.instance.GameWindow.removeSprite(bkgBlue);
        Game.instance.GameWindow.removeSprite(bkgGreen);
        Game.instance.GameWindow.removeSprite(bkgDigital);
        Game.instance.DestroyAllGameObjects();
    }
}
