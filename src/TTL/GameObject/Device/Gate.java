package TTL.GameObject.Device;

import TTL.*;
import TTL.GameObject.*;

public class Gate extends Device {
    public Gate(GameObjectData data) {
        super(data);
    }

    @Override
    protected boolean activateCondition() {
        return overlapping(Game.instance.GetPlayer());
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
