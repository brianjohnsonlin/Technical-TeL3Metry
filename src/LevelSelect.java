public class LevelSelect extends Level {
    public LevelSelect() {
        super();
        startingPoint = new Vector2(1, 16).scale(Level.MAPTOIMAGESCALE);
        ImportLevelMap("./res/levels/levelselect.png");
    }
}
