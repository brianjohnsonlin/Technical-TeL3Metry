import static org.lwjgl.glfw.GLFW.*;

public class Player extends GameObject {
    // public boolean flipped;

    public Player() {
        data = new GameObjectData(); {
            data.CollisionBoxCornerA = new Vector2(-12, -26);
            data.CollisionBoxCornerB = new Vector2(12, 0);
            data.SpriteData = new ImageData("./res/spr_char_standing_0.png"); {
                data.SpriteData.Width = 32;
                data.SpriteData.Height = 32;
                data.SpriteData.NumFrames = 1;
                data.SpriteData.NumSSColumns = 1;
                data.SpriteData.Layer = 2;
            }
            data.SpriteOffset = new Vector2(-16, -32);
        }
        Init();
    }

    public void Update() {
        // move based on key presses
        if (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT)) {
            position.x -= 2;
        }
        if (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT)) {
            position.x += 2;
        }

        super.Update();
    }
}
