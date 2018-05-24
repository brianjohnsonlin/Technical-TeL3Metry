package TTL.Level;

import TTL.*;
import TTL.GameObject.*;
import TTL.Sprite.*;

import java.awt.*;

public class Level1x3 extends Level {
    public Level1x3() {
        super();
        startingPoint = new Vector2(11, 8).scale(32);
        ImportLevelMap("./res/levels/1x3.png");

        { // Level Text
            GameObjectData data = new GameObjectData();
            TextData sprite = new TextData("Level 1-3"); {
                sprite.Layer = 4;
                sprite.Color = Color.BLACK;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(8, 8);
            gameObjectData.add(data);
        }

        { // Flavor Text
            GameObjectData data = new GameObjectData();
    		TextData sprite = new TextData("Spinning in circles!"); {
                sprite.Layer = 4;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(7, 13.25f).scale(32);
            data.Type = Device.DEVICETYPINGTEXT;
            data.Value = "128";
            gameObjectData.add(data);
        }

        { // Exit Gate
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_gate_0.png"); {
                sprite.Layer = 1;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(10, 15).scale(32);
            data.CollisionBoxCornerA = new Vector2();
            data.CollisionBoxCornerB = new Vector2(31, 31);
            data.Type = GameObject.DEVICEGATE;
            data.Value = "1-4";
            gameObjectData.add(data);
        }
    }
}
