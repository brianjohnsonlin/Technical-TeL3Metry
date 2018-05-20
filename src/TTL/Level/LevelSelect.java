package TTL.Level;

import TTL.*;
import TTL.GameObject.GameObject;
import TTL.GameObject.GameObjectData;

public class LevelSelect extends Level {
    public LevelSelect() {
        super();
        startingPoint = new Vector2(1, 16).scale(32);
        ImportLevelMap("./res/levels/levelselect.png");

        { // Return Gate
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_gate_0.png"); {
                sprite.Layer = 1;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(0, 16).scale(32);
            data.CollisionBoxCornerA = new Vector2();
            data.CollisionBoxCornerB = new Vector2(31, 31);
            data.DeviceType = GameObject.DEVICEGATE;
            data.Value = "title";
            gameObjectData.add(data);
        }

        { // Button
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_button_blue.png"); {
                sprite.Layer = 1;
                sprite.Width = 32;
                sprite.NumFrames = 2;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(1, 12).scale(32);
            data.CollisionBoxCornerA = new Vector2(4, 22);
            data.CollisionBoxCornerB = new Vector2(27, 31);
            data.DeviceType = GameObject.DEVICEBUTTON;
            data.Value = "0";
            gameObjectData.add(data);
        }

        { // Stone
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_stone_0.png"); {
                sprite.Layer = 1;
                sprite.Width = 32;
                sprite.NumFrames = 2;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(2, 9).scale(32);
            data.DeviceType = GameObject.DEVICESTONE;
            data.Value = "0";
            gameObjectData.add(data);
        }
    }
}
