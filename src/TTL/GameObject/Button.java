package TTL.GameObject;

import TTL.*;

public class Button extends Device {
    private int switchID;

    public Button(GameObjectData data) {
        super(data);
        switchID = Integer.parseInt(data.Value);
        Game.instance.SwitchIDs.putIfAbsent(switchID, false);
    }

    @Override
    protected boolean activateCondition() {
        Player player = Game.instance.GetPlayer();
        return overlapping(player) || (overlapping(player.getDuplicate()) && player.getDuplicate().IsActive());
    }

    @Override
    protected boolean deactivateCondition() {
        Player player = Game.instance.GetPlayer();
        return !(overlapping(player) || (overlapping(player.getDuplicate()) && player.getDuplicate().IsActive()));
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
