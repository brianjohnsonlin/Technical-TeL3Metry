package TTL.Level;

import TTL.*;
import TTL.GameObject.*;
import TTL.Sprite.*;

import java.awt.*;

public class Level1x5 extends Level {
    public Level1x5() {
        super();
        startingPoint = new Vector2();
        ImportLevelMap("./res/levels/1x5.png");

        { // Level Text
            GameObjectData data = new GameObjectData();
            TextData sprite = new TextData("Level 1-5"); {
                sprite.Layer = 4;
                sprite.Color = Color.BLACK;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(8, 8);
            gameObjectData.add(data);
        }

        { // Flavor Text
            GameObjectData data = new GameObjectData();
    		TextData sprite = new TextData("Return to Level Selection any time with S."); {
                sprite.Layer = 4;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(0.1f, 6.2f).scale(32);
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
            data.Value = "2-1";
            gameObjectData.add(data);
        }
    }
}
