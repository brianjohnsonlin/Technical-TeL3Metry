class GameObjectData {
    public Vector2 InitialPosition = null;
    public Vector2 CollisionBoxCornerA = null;
    public Vector2 CollisionBoxCornerB = null;
    public ImageData SpriteData = null;
    public Vector2 SpriteOffset = null;
}

public class GameObject {
    protected GameObjectData data;
    protected Vector2 position;
    protected Vector2 collisionBoxCornerA; // these are relative to position
    protected Vector2 collisionBoxCornerB; // inclusive
    protected Image sprite;
    protected Vector2 spriteOffset;

    protected GameObject() {}

    public GameObject(GameObjectData data) {
        this.data = data;
        Init();
    }

    protected void Init() {
        position = data.InitialPosition != null ? data.InitialPosition.clone() : new Vector2();
        collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
        collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
        spriteOffset = data.SpriteOffset != null ? data.SpriteOffset.clone() : new Vector2();

        // create sprite
        if (data.SpriteData != null) {
            sprite = new Image(data.SpriteData);
            sprite.position = position.add(spriteOffset);
        }
    }

    public void Update() {
        if (sprite != null) {
            sprite.position = position.add(spriteOffset);
        }
    }

    public Image GetSprite() {
        return sprite;
    }

    public void Reset() {
        if (data != null) {
            position.replaceWith(data.InitialPosition != null ? data.InitialPosition : new Vector2());
            collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
            collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
            spriteOffset.replaceWith(data.SpriteOffset != null ? data.SpriteOffset : new Vector2());
        }
        if (sprite != null) {
            sprite.Reset();
        }
    }

    public void Destroy() {
        Game.instance.DestroyGameObject(this);
    }
}
