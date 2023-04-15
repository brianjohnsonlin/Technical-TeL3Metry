package TTL.GameObject;

import TTL.*;
import TTL.Sprite.*;

import java.util.*;

public class PlayerDuplicate extends Player {
  private boolean active;
  private LinkedList<Boolean> inputs;

  public PlayerDuplicate(boolean startInverted) {
    ImageData spriteData = new ImageData("spr_dup_0.png"); {
      spriteData.Width = 32;
      spriteData.Height = 32;
      spriteData.NumFrames = 8;
      spriteData.NumSSColumns = 4;
      spriteData.Layer = 2;
    }

    defaultData = new GameObjectData(); {
      defaultData.CollisionBoxCornerA = new Vector2(4, 6);
      defaultData.CollisionBoxCornerB = new Vector2(27, 31);
      defaultData.SpriteData = spriteData;
    }

    invertedData = new GameObjectData(); {
      invertedData.CollisionBoxCornerA = new Vector2(4, 0);
      invertedData.CollisionBoxCornerB = new Vector2(27, 25);
      invertedData.SpriteData = spriteData;
    }

    data = startInverted ? invertedData : defaultData;
    defaultFrameOffset = invertedFrameOffset = 0;
    Init();

    active = sprite.Visible = false;
    inputs = null;
  }

  @Override
  public void Update() {
    if (inputs != null) {
      if (inputs.peek() != null) {
        move(inputs.poll(), inputs.poll(), inputs.poll(), inputs.poll());
      } else {
        move(false, false, false, false);
      }
    }
    updateSprite();
    sprite.Position = Position.add(spriteOffset);
  }

  public void Execute(Vector2 startingPosition, boolean startInverted, LinkedList<Boolean> inputs) {
    Invert(startInverted);
    Position.replaceWith(startingPosition);
    active = sprite.Visible = true;
    this.inputs = inputs;
  }

  @Override
  protected boolean isEmptySpace(Vector2 coord) {
    return Game.instance.GetSpaceType(coord) == (inverted ? Level.SPACE_BLACK : Level.SPACE_WHITE);
  }

  @Override
  public void Reset() {
    verticalVelocity = 0;
    Invert(Game.instance.GetCurrentLevel().GetStartInverted());
    currentFrame = 0;
    spriteOffset.replaceWith(data.SpriteOffset != null ? data.SpriteOffset : new Vector2()); // probably unnecessary
    Position.replaceWith(Game.instance.GetCurrentLevel().GetStartingPoint());
    sprite.Reset();
    active = sprite.Visible = false;
  }

  public boolean IsActive() {
    return active;
  }
}
