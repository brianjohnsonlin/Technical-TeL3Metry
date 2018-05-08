public class LevelTitle extends Level {
    public LevelTitle() {
        super();
        startingPoint = new Vector2(48, 352);
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
            data.SpriteData = new ImageData("./res/spr_exit_0.png"); {
                data.SpriteData.Layer = 1;
            }
            data.InitialPosition = new Vector2(608, 416);
//            data.SpriteData.Width = 32;
//            data.SpriteData.NumFrames = 2;
//            data.SpriteData.NumSSColumns = 2;
            gameObjectData.add(data);
        }
    }
}
