package TTL.GameObject;

import TTL.*;

public class TypingText extends Device {
    private float activationRadius;
    private String text;
    private int step;

    public TypingText(GameObjectData data) {
        super(data);
        activationRadius = Float.parseFloat(data.Value);
        text = sprite.GetState();
        sprite.SetState("");
        step = 0;
    }

    @Override
    protected boolean activateCondition() {
        Player player = Game.instance.GetPlayer();
        return Position.subtract(player.ColBoxCenterPos()).length() <= activationRadius ||
                (player.getDuplicate().IsActive() && Position.subtract(player.getDuplicate().ColBoxCenterPos()).length() <= activationRadius);
    }

    @Override
    protected boolean deactivateCondition() {
        return false; // cannot deactivate
    }

    @Override
    protected void activate() {
        step++;
    }

    @Override
    protected void deactivate() {
        // nothing
    }

    @Override
    public void Update() {
        super.Update();
        if (step > 0) {
            if (step/2 < text.length()) {
                if (step % 2 == 0) { // only call SetState when it's actually going to change
                    sprite.SetState(text.substring(0, step/2) + '|'); // incrementing the text every other frame
                }
            } else { // start ticking the ticker after text is finished typing
                if ((step - text.length() * 2) % 30 == 0) {
                    sprite.SetState(text + '|');
                } else if ((step - text.length() * 2) % 30 == 15) {
                    sprite.SetState(text);
                }
            }
            step++;
        }
    }

    @Override
    public void Reset() {
        super.Reset();
        sprite.SetState("");
        step = 0;
    }
}
