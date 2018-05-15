package TTL.GameObject.Device;

import TTL.GameObject.*;

public class Forcefield extends Device {
    public Forcefield(GameObjectData data) {
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
