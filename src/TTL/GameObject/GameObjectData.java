package TTL.GameObject;

import TTL.ImageData;
import TTL.Vector2;

public class GameObjectData {
    public Vector2 InitialPosition = null;
    public Vector2 CollisionBoxCornerA = null;
    public Vector2 CollisionBoxCornerB = null;
    public ImageData SpriteData = null;
    public Vector2 SpriteOffset = null;
    public int DeviceType = GameObject.DEVICENONE;
    public String Value = ""; // stores anything to be used by children of Device
}