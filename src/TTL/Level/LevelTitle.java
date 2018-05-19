package TTL.Level;

import TTL.*;
import TTL.GameObject.*;
import TTL.GameObject.Device.*;

public class LevelTitle extends Level {
    public LevelTitle() {
        super();
        startingPoint = new Vector2(1, 10).scale(32);
        ImportLevelMap("./res/levels/title.png");

        { // Title
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_title_0.png"); {
                sprite.Layer = 3;
            } data.SpriteData = sprite;
            gameObjectData.add(data);
        }

        { // Hello Text
            GameObjectData data = new GameObjectData();
    		TextData sprite = new TextData("Hello there, Unit L3M. Let's begin testing.");{
                sprite.Layer = 3;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(2, 14).scale(32);
            data.DeviceType = Device.DEVICETYPINGTEXT;
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
            data.DeviceType = GameObject.DEVICEGATE;
            data.Value = "levelselect";
            gameObjectData.add(data);
        }
    }
}
