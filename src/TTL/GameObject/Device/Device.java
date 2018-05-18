package TTL.GameObject.Device;

import TTL.GameObject.*;

public abstract class Device extends GameObject {
    protected boolean activated;

    public Device(GameObjectData data) {
        super(data);
        activated = false;
    }

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
    protected abstract void activate(); // TODO: going to have to figure out how to change frame with that sprite abstract class
    protected abstract void deactivate();
}
