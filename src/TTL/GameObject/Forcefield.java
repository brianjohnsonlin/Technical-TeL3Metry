package TTL.GameObject;

import TTL.*;

public class Forcefield extends GameObject {
  public Forcefield(GameObjectData data) {
    super(data);
    // mark the forcefield map on forcefield creation
    // will not change if moved, so please don't move the forcefield after creation!
    int coordX = (int)(Position.x / Game.instance.GameWindow.GetWidth() * Game.instance.CurrentLevelMap[0].length);
    int coordY = (int)(Position.y / Game.instance.GameWindow.GetHeight() * Game.instance.CurrentLevelMap.length);
    Game.instance.ForcefieldMap[coordY][coordX] = true;
  }
}
