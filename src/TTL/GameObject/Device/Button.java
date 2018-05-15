package TTL.GameObject.Device;

import TTL.GameObject.*;

public class Button extends Device {
    public Button(GameObjectData data) {
        super(data);
    }

    protected boolean activateCondition() {
        return false;
    }

    protected boolean deactivateCondition() {
        return false;
    }

    protected void activate() {

    }

    protected void deactivate() {

    }
}
