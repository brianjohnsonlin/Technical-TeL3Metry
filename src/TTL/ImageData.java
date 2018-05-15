package TTL;

public class ImageData {
    public String Filename;
    public float Width = -1; // -1 will use actual resolution
    public float Height = -1;
    public int NumFrames = 1;
    public int NumSSColumns = 1; // number of sprite sheet columns (don't really need to know the number of rows)
    public int Layer = 0;
    // Layers:
    // 0 Background
    // 1 Devices
    // 2 Player
    // 3 Title, Text, Foreground

    public ImageData(String filename) {
        Filename = filename;
    }
}
