package TTL.Level;

import TTL.*;
import TTL.GameObject.GameObject;
import TTL.GameObject.GameObjectData;

public class LevelTitle extends Level {
    public LevelTitle() {
        super();
        startingPoint = new Vector2(1, 10).scale(Level.MAPTOIMAGESCALE);
        ImportLevelMap("./res/levels/title.png");

        { // Title
            GameObjectData data = new GameObjectData();
            data.SpriteData = new ImageData("./res/spr_title_0.png"); {
                data.SpriteData.Layer = 3;
            }
            gameObjectData.add(data);
        }

        { // Exit Gate
            GameObjectData data = new GameObjectData();
            data.SpriteData = new ImageData("./res/spr_gate_0.png"); {
                data.SpriteData.Layer = 1;
            }
            data.InitialPosition = new Vector2(19, 13).scale(Level.MAPTOIMAGESCALE);
            data.CollisionBoxCornerA = new Vector2();
            data.CollisionBoxCornerB = new Vector2(31, 31);
            data.DeviceType = GameObject.DEVICEGATE;
            data.Value = "levelselect";
            gameObjectData.add(data);
        }
    }
}
