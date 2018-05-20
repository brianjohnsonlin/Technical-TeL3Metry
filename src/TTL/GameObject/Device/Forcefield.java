package TTL.GameObject.Device;

import TTL.GameObject.*;

public class Forcefield extends Device {
    public Forcefield(GameObjectData data) {
        super(data);
    }

    @Override
    protected boolean activateCondition() {
        return false;
    }

    @Override
    protected boolean deactivateCondition() {
        return false;
    }

    @Override
    protected void activate() {

    }

    @Override
    protected void deactivate() {

    }
}
