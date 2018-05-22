package TTL.GameObject;

import TTL.Game;

public class Hint extends Device {
    private final float FRAMESPEED = 1f/15f;

    private int triggerKey;
    private float currentFrame;

    public Hint(GameObjectData data) {
        super(data);
        triggerKey = (int)Float.parseFloat(data.Value); // this is a float typecasted back to int because MoveHint uses Value as a float
        currentFrame = 0;
        Position.replaceWith(Game.instance.GetPlayer().Position);
    }

    @Override
    public void Update() {
        Position.replaceWith(Game.instance.GetPlayer().Position);
        super.Update();
        sprite.Visible = !activated;
        if (!activated) {
            sprite.SetState("++" + (int)currentFrame);
            currentFrame = (currentFrame + FRAMESPEED) % 2;
        }
    }

    @Override
    protected boolean activateCondition() {
        return Game.instance.GameWindow.GetKeyHeld(triggerKey);
    }

    @Override
    protected boolean deactivateCondition() {
        return false; // cannot deactivate
    }

    @Override
    protected void activate() {
        // nothing - the Update function will take care of hiding the sprite
    }

    @Override
    protected void deactivate() {
        // nothing
    }

    @Override
    public void Reset() {
        super.Reset();
        Position.replaceWith(Game.instance.GetPlayer().Position);
        sprite.Position = Position.add(spriteOffset);
        sprite.Visible = true;
        currentFrame = 0;
        sprite.SetState("++0");
    }
}
