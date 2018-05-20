package TTL.Sprite;

import TTL.*;

public abstract class Sprite {
    public boolean Visible = true;
    public boolean AddedToDisplay = false;
    public Vector2 Position = new Vector2();
    protected int layer;

    public abstract void Render();
    public abstract void Reset();
    public abstract void SetState(String state);
    public abstract String GetState();

    public int GetLayer() {
        return layer;
    }
}
