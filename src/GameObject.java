class GameObjectData {
    public Vector2 InitialPosition = null;
    public Vector2 CollisionBoxCornerA = null;
    public Vector2 CollisionBoxCornerB = null;
    public ImageData SpriteData = null;
    public Vector2 SpriteOffset = null;
    public int DeviceType = GameObject.DEVICENONE;
    public String Value = ""; // stores anything to be used by children of Device
}

public class GameObject {
    public static final int DEVICENONE = 0;
    public static final int DEVICEGATE = 1;
    public static final int DEVICEBUTTON = 2;
    public static final int DEVICESTONE = 3;
    public static final int DEVICEFORCEFIELD = 4;
    public static final int DEVICETEXT = 5;

    public boolean SlatedForDestruction;
    public Vector2 Position;

    protected GameObjectData data;
    protected Vector2 collisionBoxCornerA; // these are relative to position
    protected Vector2 collisionBoxCornerB; // inclusive
    protected Image sprite;
    protected Vector2 spriteOffset;

    protected GameObject() {}

    public GameObject(GameObjectData data) {
        this.data = data;
        Init();
        SlatedForDestruction = false;
    }

    protected void Init() {
        Position = data.InitialPosition != null ? data.InitialPosition.clone() : new Vector2();
        collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
        collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
        spriteOffset = data.SpriteOffset != null ? data.SpriteOffset.clone() : new Vector2();

        // create sprite
        if (data.SpriteData != null) {
            sprite = new Image(data.SpriteData);
            sprite.Position = Position.add(spriteOffset);
        }
    }

    public void Update() {
        if (sprite != null) {
            sprite.Position = Position.add(spriteOffset);
        }
    }

    public Image GetSprite() {
        return sprite;
    }

    public void Reset() {
        if (data != null) {
            Position.replaceWith(data.InitialPosition != null ? data.InitialPosition : new Vector2());
            collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
            collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
            spriteOffset.replaceWith(data.SpriteOffset != null ? data.SpriteOffset : new Vector2());
        }
        if (sprite != null) {
            sprite.Reset();
        }
    }

    public Vector2 ColBoxTopLeftPos() {
        return Position.add(collisionBoxCornerA);
    }

    public Vector2 ColBoxTopRightPos() {
        return Position.add(new Vector2(collisionBoxCornerB.x, collisionBoxCornerA.y));
    }

    public Vector2 ColBoxBottomLeftPos() {
        return Position.add(new Vector2(collisionBoxCornerA.x, collisionBoxCornerB.y));
    }

    public Vector2 ColBoxBottomRightPos() {
        return Position.add(collisionBoxCornerB);
    }

    protected boolean overlapping(GameObject other) {
        // if one or the other doesn't have collision
        if (collisionBoxCornerA == null || collisionBoxCornerB == null ||
            other.collisionBoxCornerA == null || other.collisionBoxCornerB == null) {
            return false;
        }

        // if one collision is below or to the left of the other collision
        if (ColBoxTopLeftPos().x > other.ColBoxBottomRightPos().x || ColBoxBottomRightPos().x < other.ColBoxTopLeftPos().x ||
            ColBoxTopLeftPos().y > other.ColBoxBottomRightPos().y || ColBoxBottomRightPos().y < other.ColBoxTopLeftPos().y) {
            return false;
        }

        return true;
    }
}
