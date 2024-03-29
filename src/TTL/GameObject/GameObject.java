package TTL.GameObject;

import TTL.*;
import TTL.Sprite.*;

public class GameObject {
  public static final int NONE = 0;
  public static final int DEVICEGATE = 1;
  public static final int DEVICEBUTTON = 2;
  public static final int DEVICESTONE = 3;
  public static final int DEVICETYPINGTEXT = 4;
  public static final int FORCEFIELD = 5;
  public static final int DEVICEHINT = 6;
  public static final int DEVICEMOVEHINT = 7;

  public boolean SlatedForDestruction = false;
  public Vector2 Position;

  protected GameObjectData data;
  protected Vector2 collisionBoxCornerA; // these are relative to position
  protected Vector2 collisionBoxCornerB; // inclusive
  protected Sprite sprite;
  protected Vector2 spriteOffset;

  protected GameObject() {}

  public GameObject(GameObjectData data) {
    this.data = data;
    Init();
  }

  protected void Init() {
    Position = data.InitialPosition != null ? data.InitialPosition.clone() : new Vector2();
    collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
    collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
    spriteOffset = data.SpriteOffset != null ? data.SpriteOffset.clone() : new Vector2();

    // create sprite
    if (data.SpriteData != null) {
      sprite = data.SpriteData.Instantiate();
      sprite.Position = Position.add(spriteOffset);
    }
  }

  public void Update() {
    if (sprite != null) {
      sprite.Position = Position.add(spriteOffset);
    }
  }

  public Sprite GetSprite() {
    return sprite;
  }

  public void Reset() { // for subclasses of temp gameobjects (such as projectiles), override Reset to delete itself
    if (data != null) {
      Position.replaceWith(data.InitialPosition != null ? data.InitialPosition : new Vector2());
      collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
      collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
      spriteOffset.replaceWith(data.SpriteOffset != null ? data.SpriteOffset : new Vector2());
    }
    if (sprite != null) {
      sprite.Reset();
      sprite.Position = Position.add(spriteOffset);
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

  public Vector2 ColBoxCenterPos() {
    return Position.add(collisionBoxCornerA.lerp(collisionBoxCornerB, 0.5f));
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

  protected boolean overlapping(Vector2 point) {
    if (collisionBoxCornerA == null || collisionBoxCornerB == null) {
      return false;
    }

    return point.x >= collisionBoxCornerA.x + Position.x && point.x <= collisionBoxCornerB.x + Position.x &&
         point.y >= collisionBoxCornerA.y + Position.y && point.y <= collisionBoxCornerB.y + Position.y;
  }
}
