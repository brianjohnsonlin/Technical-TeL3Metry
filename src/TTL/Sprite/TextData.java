package TTL.Sprite;

public class TextData extends SpriteData {
  public String Text;
  public String FontName = "Opificio";
  public float FontSize = 1.1f;
  public java.awt.Color Color = java.awt.Color.WHITE;
  public float MaxLineLength = 1000;
  public boolean Centered = false;

  public TextData(String text) {
    Text = text;
  }

  @Override
  public Sprite Instantiate() {
    return new Text(this);
  }
}
