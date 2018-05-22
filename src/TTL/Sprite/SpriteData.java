package TTL.Sprite;

public abstract class SpriteData {
    public int Layer = 0;
    // Layers:
    // 0 Background
    // 1 Devices
    // 2 PlayerDuplicate
    // 3 Player
    // 4 Title, Text, Foreground

    public abstract Sprite Instantiate();
}
