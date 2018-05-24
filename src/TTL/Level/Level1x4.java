package TTL.Level;

import TTL.GameObject.Device;
import TTL.GameObject.GameObject;
import TTL.GameObject.GameObjectData;
import TTL.Sprite.ImageData;
import TTL.Sprite.TextData;
import TTL.Vector2;

import java.awt.*;

public class Level1x4 extends Level {
    public Level1x4() {
        super();
        startingPoint = new Vector2(0.5f, 1).scale(32);
        ImportLevelMap("./res/levels/1x4.png");

        { // Level Text
            GameObjectData data = new GameObjectData();
            TextData sprite = new TextData("Level 1-4"); {
                sprite.Layer = 4;
                sprite.Color = Color.BLACK;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(8, 8);
            gameObjectData.add(data);
        }

        { // Flavor Text
            GameObjectData data = new GameObjectData();
    		TextData sprite = new TextData("Remember that you\ncan restart with R."); {
                sprite.Layer = 4;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(4.5f, 17.5f).scale(32);
            data.Type = Device.DEVICETYPINGTEXT;
            data.Value = "128";
            gameObjectData.add(data);
        }

        { // Exit Gate
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_gate_0.png"); {
                sprite.Layer = 1;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(19, 18).scale(32);
            data.CollisionBoxCornerA = new Vector2();
            data.CollisionBoxCornerB = new Vector2(31, 31);
            data.Type = GameObject.DEVICEGATE;
            data.Value = "1-5";
            gameObjectData.add(data);
        }
    }
}
