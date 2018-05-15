package TTL.GameObject.Device;

import TTL.*;
import TTL.GameObject.*;

public class Gate extends Device {
    public Gate(GameObjectData data) {
        super(data);
    }

    protected boolean activateCondition() {
        return overlapping(Game.instance.GetPlayer());
    }

    protected boolean deactivateCondition() {
        return false; // cannot deactivate
    }

    protected void activate() {
        Game.instance.ChangeLevel(data.Value);
    }

    protected void deactivate() {
        // nothing
    }
}
