public class DeviceGate extends Device {
    public DeviceGate(GameObjectData data) {
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
