package TTL.GameObject.Device;

import TTL.*;
import TTL.GameObject.*;

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

    protected boolean activateCondition() {
        return Position.subtract(Game.instance.GetPlayer().ColBoxCenterPos()).length() <= activationRadius;
    }

    protected boolean deactivateCondition() {
        return false; // cannot deactivate
    }

    protected void activate() {
        step++;
    }

    protected void deactivate() {
        // nothing
    }

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

    public void Reset() {
        super.Reset();
        sprite.SetState("");
        step = 0;
    }
}
