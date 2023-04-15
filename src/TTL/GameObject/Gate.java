package TTL.GameObject;

import TTL.*;

public class Gate extends Device {
  public Gate(GameObjectData data) {
    super(data);
  }

  @Override
  protected boolean activateCondition() {
    return overlapping(Game.instance.GetPlayer()) || overlapping(Game.instance.GetPlayer().getDuplicate());
  }

  @Override
  protected boolean deactivateCondition() {
    return false; // cannot deactivate
  }

  @Override
  protected void activate() {
    Game.instance.ChangeLevel(data.Value);
  }

  @Override
  protected void deactivate() {
    // nothing
  }
}
