package TTL.Level;

import TTL.GameObject.Device;
import TTL.GameObject.GameObject;
import TTL.GameObject.GameObjectData;
import TTL.Sprite.ImageData;
import TTL.Sprite.TextData;
import TTL.Vector2;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;

public class Level1x1 extends Level {
    public Level1x1() {
        super();
        startingPoint = new Vector2(0.5f, 10).scale(32);
        ImportLevelMap("./res/levels/1x1.png");

        { // Level Text
            GameObjectData data = new GameObjectData();
            TextData sprite = new TextData("Level 1-1"); {
                sprite.Layer = 4;
                sprite.Color = Color.BLACK;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(8, 8);
            gameObjectData.add(data);
        }

        { // Flavor Text
            GameObjectData data = new GameObjectData();
    		TextData sprite = new TextData("Overcoming an obstacle is a\nmatter of seeing both sides.");{
                sprite.Layer = 4;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(4, 12).scale(32);
            data.Type = Device.DEVICETYPINGTEXT;
            data.Value = "640";
            gameObjectData.add(data);
        }

        { // Exit Gate
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_gate_0.png"); {
                sprite.Layer = 1;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(18, 8).scale(32);
            data.CollisionBoxCornerA = new Vector2();
            data.CollisionBoxCornerB = new Vector2(31, 31);
            data.Type = GameObject.DEVICEGATE;
            data.Value = "1-2";
            gameObjectData.add(data);
        }

        { // F Hint
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_hint_f.png"); {
                sprite.Layer = 2;
                sprite.NumFrames = 2;
            } data.SpriteData = sprite;
            data.SpriteOffset = new Vector2(24, -24);
            data.Type = GameObject.DEVICEHINT;
            data.Value = "" + GLFW_KEY_F;
            gameObjectData.add(data);
        }
    }
}
