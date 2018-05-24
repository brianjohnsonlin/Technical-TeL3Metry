package TTL.Level;

import TTL.*;
import TTL.GameObject.*;
import TTL.Sprite.*;

import java.awt.*;

public class LevelSelect extends Level {
    public LevelSelect() {
        super();
        startingPoint = new Vector2(1, 16).scale(32);
        ImportLevelMap("./res/levels/levelselect.png");

        { // Select a level Text
            GameObjectData data = new GameObjectData();
            TextData sprite = new TextData("Select a level."); {
                sprite.Layer = 4;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(70, 552);
            data.Type = Device.DEVICETYPINGTEXT;
            data.Value = "128";
            gameObjectData.add(data);
        }

        { // Mute music text
            GameObjectData data = new GameObjectData();
            TextData sprite = new TextData("M to toggle music."); {
                sprite.Layer = 4;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(400, 580);
            data.Type = Device.DEVICETYPINGTEXT;
            data.Value = "640";
            gameObjectData.add(data);
        }

        { // Return Gate
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_gate_0.png"); {
                sprite.Layer = 1;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(0, 16).scale(32);
            data.CollisionBoxCornerA = new Vector2();
            data.CollisionBoxCornerB = new Vector2(31, 31);
            data.Type = GameObject.DEVICEGATE;
            data.Value = "title";
            gameObjectData.add(data);
        }

        { // Button
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_button_blue.png"); {
                sprite.Layer = 1;
                sprite.NumFrames = 2;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(1, 12).scale(32);
            data.CollisionBoxCornerA = new Vector2(4, 22);
            data.CollisionBoxCornerB = new Vector2(27, 31);
            data.Type = GameObject.DEVICEBUTTON;
            data.Value = "0";
            gameObjectData.add(data);
        }

        { // Stone
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_stone_0.png"); {
                sprite.Layer = 1;
                sprite.NumFrames = 2;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(2, 9).scale(32);
            data.Type = GameObject.DEVICESTONE;
            data.Value = "0";
            gameObjectData.add(data);
        }

        { // Stone
            GameObjectData data = new GameObjectData();
            ImageData sprite = new ImageData("./res/spr_stone_0.png"); {
                sprite.Layer = 1;
                sprite.NumFrames = 2;
            } data.SpriteData = sprite;
            data.InitialPosition = new Vector2(4, 12).scale(32);
            data.Type = GameObject.DEVICESTONE;
            data.Value = "0";
            gameObjectData.add(data);
        }

        for (int i = 0; i < 15; i++) { // Level Gates
            Vector2 pos;
            String levelname = "" + (i/5+1) + "-" + (i%5+1);
            if (i < 5) {
                pos = new Vector2(5 + i*2, 15).scale(32);
            } else if (i > 9) {
                pos = new Vector2(6 + (i-10)*2, 5).scale(32);
            } else {
                pos = new Vector2(15 - (i-5)*2, 10).scale(32);
            }

            { // Level Gate
                GameObjectData data = new GameObjectData();
                ImageData sprite = new ImageData("./res/spr_gate_0.png"); {
                    sprite.Layer = 1;
                } data.SpriteData = sprite;
                data.InitialPosition = pos;
                data.CollisionBoxCornerA = new Vector2();
                data.CollisionBoxCornerB = new Vector2(31, 31);
                data.Type = GameObject.DEVICEGATE;
                data.Value = levelname;
                gameObjectData.add(data);
            }

            { // Label
                GameObjectData data = new GameObjectData();
                TextData sprite = new TextData(levelname); {
                    sprite.Layer = 1;
                    sprite.Color = Color.BLACK;
                } data.SpriteData = sprite;
                data.InitialPosition = pos;
                gameObjectData.add(data);
            }
        }
    }
}
