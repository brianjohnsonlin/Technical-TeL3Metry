package TTL;

public class ImageData extends SpriteData {
    public String Filename;
    public float Width = -1; // -1 will use actual resolution
    public float Height = -1;
    public int NumFrames = 1;
    public int NumSSColumns = 1; // number of sprite sheet columns (don't really need to know the number of rows)

    public ImageData(String filename) {
        Filename = filename;
    }

    public Sprite Instantiate() {
        return new Image(this);
    }
}
