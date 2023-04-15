package TTL.GameObject;

public abstract class Device extends GameObject {
  protected boolean activated;

  public Device(GameObjectData data) {
    super(data);
    activated = false;
  }

  @Override
  public void Update() {
    if (activateCondition() && !activated) {
      activated = true;
      activate();
    } else if (deactivateCondition() && activated) {
      activated = false;
      deactivate();
    }
    super.Update();
  }

  protected abstract boolean activateCondition();
  protected abstract boolean deactivateCondition();
  protected abstract void activate();
  protected abstract void deactivate();

  @Override
  public void Reset() {
    super.Reset();
    activated = false;
  }
}
