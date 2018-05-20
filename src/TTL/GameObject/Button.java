package TTL.GameObject;

import TTL.*;

public class Button extends Device {
    private int switchID;

    public Button(GameObjectData data) {
        super(data);
        switchID = Integer.parseInt(data.Value);
        Game.instance.SwitchIDs.putIfAbsent(switchID, false);
        sprite.SetState("++0");
    }

    @Override
    protected boolean activateCondition() {
        return overlapping(Game.instance.GetPlayer());
    }

    @Override
    protected boolean deactivateCondition() {
        return !overlapping(Game.instance.GetPlayer());
    }

    @Override
    protected void activate() {
        Game.instance.SwitchIDs.put(switchID, true);
        sprite.SetState("++1");
    }

    @Override
    protected void deactivate() {
        Game.instance.SwitchIDs.put(switchID, false);
        sprite.SetState("++0");
    }

    @Override
    public void Reset() {
        super.Reset();
        Game.instance.SwitchIDs.put(switchID, false);
        sprite.SetState("++0");
    }
}
