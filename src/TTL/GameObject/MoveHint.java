package TTL.GameObject;

import TTL.Game;

import static org.lwjgl.glfw.GLFW.*;

public class MoveHint extends Hint {
    private float hintDelay;

    public MoveHint(GameObjectData data) {
        super(data);
        hintDelay = Float.parseFloat(data.Value);
    }

    @Override
    public void Update() {
        super.Update();
        if (hintDelay > 0) {
            sprite.Visible = false;
            hintDelay -= 1f / 30f;
        }
    }

    @Override
    protected boolean activateCondition() {
        return Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) || Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT) || Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_SPACE);
    }

    @Override
    public void Reset() {
        super.Reset();
        hintDelay = Float.parseFloat(data.Value);
        sprite.Visible = false;
    }
}
