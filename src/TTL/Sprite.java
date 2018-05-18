package TTL;

public abstract class Sprite {
    public boolean Visible = true;
    public boolean AddedToDisplay = false;
    public Vector2 Position = new Vector2();
    protected int layer;

    public abstract void Render();
    public abstract void Reset();
    // TODO: advance state method for advancing frames or typing letters?

    public int GetLayer() {
        return layer;
    }
}
