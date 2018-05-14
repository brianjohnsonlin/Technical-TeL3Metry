public class LevelSelect extends Level {
    public LevelSelect() {
        super();
        startingPoint = new Vector2(1, 16).scale(Level.MAPTOIMAGESCALE);
        ImportLevelMap("./res/levels/levelselect.png");

        { // Exit Gate
            GameObjectData data = new GameObjectData();
            data.SpriteData = new ImageData("./res/spr_gate_0.png"); {
                data.SpriteData.Layer = 1;
            }
            data.InitialPosition = new Vector2(0, 16).scale(Level.MAPTOIMAGESCALE);
            data.CollisionBoxCornerA = new Vector2();
            data.CollisionBoxCornerB = new Vector2(31, 31);
            data.DeviceType = GameObject.DEVICEGATE;
            data.Value = "title";
            gameObjectData.add(data);
        }
    }
}
