import java.util.*;

public class LevelTitle extends Level {
    public LevelTitle() {
        startingPoint = new Vector2(32, 320);
        ImportLevelMap("./res/levels/title.png");
        gameObjects = new ArrayList<GameObject>();
        gameObjects.add(new GameObject("./res/spr_title_0.png", 0, 0));
    }
}
