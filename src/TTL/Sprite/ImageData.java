package TTL.Sprite;

public class ImageData extends SpriteData {
  public String Filename;
  public float Width = -1; // -1 will use actual resolution
  public float Height = -1;
  public int NumFrames = 1;
  // Number of sprite sheet columns (don't really need to know the number of rows)
  // This will default to NumFrames if not set. basically assumes everything is on one row
  public int NumSSColumns = -1;
  public ImageData(String filename) {
    Filename = filename;
  }

  @Override
  public Sprite Instantiate() {
    return new Image(this);
  }
}
