package TTL.GameObject.Device;

import TTL.*;
import TTL.GameObject.*;
import TTL.Level.*;

public class Stone extends Device {
    private int switchID;
    private int coordX;
    private int coordY;
    private int originalSpaceType;
    private String offState;
    private String onState;

    public Stone(GameObjectData data) {
        super(data);
        switchID = Integer.parseInt(data.Value);
        Game.instance.SwitchIDs.putIfAbsent(switchID, false);
        coordX = (int)(Position.x / Game.instance.GameWindow.GetWidth() * Game.instance.CurrentLevelMap[0].length);
        coordY = (int)(Position.y / Game.instance.GameWindow.GetHeight() * Game.instance.CurrentLevelMap.length);
        originalSpaceType = Game.instance.GetSpaceType(Position);
        switch (originalSpaceType) {
            case Level.SPACE_WHITE:
                offState = "++0";
                onState = "++1";
                break;
            case Level.SPACE_BLACK:
                offState = "++1";
                onState = "++0";
                break;
            default:
                offState = "++0";
                onState = "++0";
                break;
        }
        sprite.SetState(offState);
    }

    protected boolean activateCondition() {
        return Game.instance.SwitchIDs.get(switchID);
    }

    protected boolean deactivateCondition() {
        return !Game.instance.SwitchIDs.get(switchID);
    }

    protected void activate() {
        switch (originalSpaceType) {
            case Level.SPACE_WHITE:
                Game.instance.CurrentLevelMap[coordY][coordX] = Level.SPACE_BLACK;
                break;
            case Level.SPACE_BLACK:
                Game.instance.CurrentLevelMap[coordY][coordX] = Level.SPACE_WHITE;
                break;
        }
        sprite.SetState(onState);
    }

    protected void deactivate() {
        Game.instance.CurrentLevelMap[coordY][coordX] = originalSpaceType;
        sprite.SetState(offState);
    }

    public void Reset() {
        super.Reset();
        Game.instance.CurrentLevelMap[coordY][coordX] = originalSpaceType;
        sprite.SetState(offState);
    }
}
