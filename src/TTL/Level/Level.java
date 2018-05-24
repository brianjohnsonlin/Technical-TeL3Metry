package TTL.Level;

import TTL.*;
import TTL.GameObject.*;
import TTL.Sprite.*;
import org.json.*;

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
        startingPoint = new Vector2();
        gameObjectData = new ArrayList<>();
        startInverted = false;
    }

    public Level(JSONObject obj) {
        if (obj.has("LevelMap")) {
            ImportLevelMap(obj.getString("LevelMap"));
        }
        if (obj.has("StartingPoint")) {
            startingPoint = ToVector2(obj.getJSONObject("StartingPoint"));
        } else {
            startingPoint = new Vector2();
        }
        startInverted = obj.has("StartInverted") && obj.getBoolean("StartInverted"); // obj.has("StartInverted") ? obj.getBoolean("StartInverted") : false;
        gameObjectData = new ArrayList<>();

        if (obj.has("GameObjects")) {
            JSONArray levelArray = obj.getJSONArray("GameObjects");
            for (int i = 0; i < levelArray.length(); i++) {
                JSONObject gameObj = levelArray.getJSONObject(i);
                GameObjectData data = new GameObjectData();
                if (gameObj.has("InitialPosition")) data.InitialPosition = ToVector2(gameObj.getJSONObject("InitialPosition"));
                if (gameObj.has("CollisionBoxCornerA")) data.CollisionBoxCornerA = ToVector2(gameObj.getJSONObject("CollisionBoxCornerA"));
                if (gameObj.has("CollisionBoxCornerB")) data.CollisionBoxCornerA = ToVector2(gameObj.getJSONObject("CollisionBoxCornerB"));
                SpriteData sprite = null;
                if (gameObj.has("Sprite")) {
                    JSONObject spriteObj = gameObj.getJSONObject("Sprite");
                    if (spriteObj.has("Type")) {
                        if (spriteObj.getString("Type").equals("image")) {
                            ImageData image = new ImageData(spriteObj.getString("Path"));
                            if (spriteObj.has("Width")) image.Width = spriteObj.getFloat("Width");
                            if (spriteObj.has("Height")) image.Height = spriteObj.getFloat("Height");
                            if (spriteObj.has("NumFrames")) image.NumFrames = spriteObj.getInt("NumFrames");
                            if (spriteObj.has("NumSSColumns")) image.NumSSColumns = spriteObj.getInt("NumSSColumns");
                            if (spriteObj.has("Layer")) image.Layer = spriteObj.getInt("Layer");
                            sprite = image;
                        } else if (spriteObj.getString("Type").equals("text")) {
                            TextData text = new TextData(spriteObj.getString("Text"));
                            if (spriteObj.has("FontName")) text.FontName = spriteObj.getString("FontName");
                            if (spriteObj.has("FontSize")) text.FontSize = spriteObj.getFloat("FontSize");
                            if (spriteObj.has("Color")) text.Color = ToColor(spriteObj.getJSONObject("Color"));
                            if (spriteObj.has("MaxLineLength")) text.MaxLineLength = spriteObj.getFloat("MaxLineLength");
                            if (spriteObj.has("Centered")) text.Centered = spriteObj.getBoolean("Centered");
                            if (spriteObj.has("Layer")) text.Layer = spriteObj.getInt("Layer");
                            sprite = text;
                        }
                    }
                }
                data.SpriteData = sprite;
                if (gameObj.has("SpriteOffset")) data.SpriteOffset = ToVector2(gameObj.getJSONObject("SpriteOffset"));
                if (gameObj.has("Type")) {
                    switch (gameObj.getString("Type")) {
                        case "DEVICEGATE":
                            data.Type = GameObject.DEVICEGATE;
                            break;
                        case "DEVICEBUTTON":
                            data.Type = GameObject.DEVICEBUTTON;
                            break;
                        case "DEVICESTONE":
                            data.Type = GameObject.DEVICESTONE;
                            break;
                        case "DEVICETYPINGTEXT":
                            data.Type = GameObject.DEVICETYPINGTEXT;
                            break;
                        case "FORCEFIELD":
                            data.Type = GameObject.FORCEFIELD;
                            break;
                        case "DEVICEHINT":
                            data.Type = GameObject.DEVICEHINT;
                            break;
                        case "DEVICEMOVEHINT":
                            data.Type = GameObject.DEVICEMOVEHINT;
                            break;
                        default:
                            data.Type = GameObject.NONE;
                            break;
                    }
                }
                if (gameObj.has("Value")) data.Value = gameObj.getString("Value");
                gameObjectData.add(data);
            }
        }
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

    public static Vector2 ToVector2(JSONObject obj) {
        return new Vector2(obj.getFloat("x"), obj.getFloat("y"));
    }

    public static java.awt.Color ToColor(JSONObject obj) {
        return new java.awt.Color(obj.getInt("r") / 255f, obj.getInt("g") / 255f, obj.getInt("b") / 255f);
    }
}
