package TTL.Level;

import TTL.*;
import TTL.GameObject.*;
import TTL.Sprite.*;

import static org.lwjgl.glfw.GLFW.*;

public class LevelTitle extends Level {
    public LevelTitle() {
        super();
        startingPoint = new Vector2(1, 10).scale(32);
        ImportLevelMap("./res/levels/title.png");

        { // Title
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_title_0.png"); {
                sprite.Layer = 4;
            } data.SpriteData = sprite;
            gameObjectData.add(data);
        }

        { // Hello Text
            GameObjectData data = new GameObjectData();
    		TextData sprite = new TextData("Hello there, Unit L3M. Let's begin testing.");{
                sprite.Layer = 4;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(2, 14).scale(32);
            data.Type = Device.DEVICETYPINGTEXT;
            data.Value = "128";
            gameObjectData.add(data);
        }

        { // Exit Gate
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_gate_0.png"); {
                sprite.Layer = 1;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(19, 13).scale(32);
            data.CollisionBoxCornerA = new Vector2();
            data.CollisionBoxCornerB = new Vector2(31, 31);
            data.Type = GameObject.DEVICEGATE;
            data.Value = "levelselect";
            gameObjectData.add(data);
        }

        { // Move Hint
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_hint_move.png"); {
                sprite.Layer = 2;
                sprite.NumFrames = 2;
            } data.SpriteData = sprite;
            data.SpriteOffset = new Vector2(24, -24);
            data.Type = GameObject.DEVICEMOVEHINT;
            data.Value = "" + 5f;
            gameObjectData.add(data);
        }
    }
}
