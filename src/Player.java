import static org.lwjgl.glfw.GLFW.*;

public class Player extends GameObject {
    private final float MOVEMENTSPEED = 2;
    private final float JUMPVELOCITY = 7.5f;
    private final float DOWNWARDACCELERATION = 0.5f;

    private float verticalVelocity;

    public Player() {
        data = new GameObjectData(); {
            data.CollisionBoxCornerA = new Vector2(4, 6);
            data.CollisionBoxCornerB = new Vector2(27, 31);
            data.SpriteData = new ImageData("./res/spr_char_standing_0.png"); {
                data.SpriteData.Width = 32;
                data.SpriteData.Height = 32;
                data.SpriteData.NumFrames = 1;
                data.SpriteData.NumSSColumns = 1;
                data.SpriteData.Layer = 2;
            }
        }
        Init();

        verticalVelocity = 0;
    }

    public void Update() {
        if (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT)) {
            position.x -= MOVEMENTSPEED;
            while (!Game.instance.GetCurrentLevel().IsEmptySpace(position.add(collisionBoxCornerA))) {
                position.x += 1;
            }
        }
        if (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT)) {
            position.x += MOVEMENTSPEED;
            while (!Game.instance.GetCurrentLevel().IsEmptySpace(position.add(collisionBoxCornerB))) {
                position.x -= 1;
            }
        }

        if (IsGrounded()) {
            if (Game.instance.GameWindow.GetKeyDown(GLFW_KEY_SPACE)) {
                verticalVelocity -= JUMPVELOCITY;
            }
        } else {
            verticalVelocity += DOWNWARDACCELERATION;
        }

        if (verticalVelocity > 0) {
            position.y += verticalVelocity;
            while (!Game.instance.GetCurrentLevel().IsEmptySpace(position.add(collisionBoxCornerB)) ||
                   !Game.instance.GetCurrentLevel().IsEmptySpace(position.add(new Vector2(collisionBoxCornerA.x, collisionBoxCornerB.y)))) {
                position.y -= 1;
                verticalVelocity = 0;
            }
        } else if (verticalVelocity < 0) {
            position.y += verticalVelocity;
            while (!Game.instance.GetCurrentLevel().IsEmptySpace(position.add(collisionBoxCornerA)) ||
                   !Game.instance.GetCurrentLevel().IsEmptySpace(position.add(new Vector2(collisionBoxCornerB.x, collisionBoxCornerA.y)))) {
                position.y += 1;
                verticalVelocity = 0;
            }
        }

        super.Update();
    }

    // only checks bottom corners because nothing is thinner than L3M
    public boolean IsGrounded() {
        if (!Game.instance.GetCurrentLevel().IsEmptySpace(new Vector2(collisionBoxCornerA.x, collisionBoxCornerB.y + 1).add(position)) ||
            !Game.instance.GetCurrentLevel().IsEmptySpace(new Vector2(collisionBoxCornerB.x, collisionBoxCornerB.y + 1).add(position))) {
            return true;
        }
        return false;
    }
}
