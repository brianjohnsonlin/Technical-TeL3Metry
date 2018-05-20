package TTL.GameObject.Device;

import TTL.*;
import TTL.GameObject.*;

public class Button extends Device {
    private int switchID;

    public Button(GameObjectData data) {
        super(data);
        switchID = Integer.parseInt(data.Value);
        Game.instance.SwitchIDs.putIfAbsent(switchID, false);
        sprite.SetState("++0");
    }

    protected boolean activateCondition() {
        return overlapping(Game.instance.GetPlayer());
    }

    protected boolean deactivateCondition() {
        return !overlapping(Game.instance.GetPlayer());
    }

    protected void activate() {
        Game.instance.SwitchIDs.put(switchID, true);
        sprite.SetState("++1");
    }

    protected void deactivate() {
        Game.instance.SwitchIDs.put(switchID, false);
        sprite.SetState("++0");
    }

    public void Reset() {
        super.Reset();
        Game.instance.SwitchIDs.put(switchID, false);
        sprite.SetState("++0");
    }
}
