import static org.lwjgl.glfw.GLFW.*;

public class Player extends GameObject {
    private final float MOVEMENTSPEED = 2;
    private final float JUMPVELOCITY = 7.5f;
    private final float DOWNWARDACCELERATION = 0.5f;

    private GameObjectData defaultData;
    private GameObjectData invertedData;
    private boolean inverted;
    private int frameOffset;
    private float verticalVelocity;

    public Player() {
        ImageData sprite = new ImageData("./res/spr_char_standing_0.png"); {
            sprite.Width = 32;
            sprite.Height = 32;
            sprite.NumFrames = 1;
            sprite.NumSSColumns = 1;
            sprite.Layer = 2;
        }

        defaultData = new GameObjectData(); {
            defaultData.CollisionBoxCornerA = new Vector2(4, 6);
            defaultData.CollisionBoxCornerB = new Vector2(27, 31);
            defaultData.SpriteData = sprite;
        }

        invertedData = new GameObjectData(); {
            invertedData.CollisionBoxCornerA = new Vector2(4, 0);
            invertedData.CollisionBoxCornerB = new Vector2(27, 25);
            invertedData.SpriteData = sprite;
        }

        data = defaultData;
        Init();

        inverted = false;
        frameOffset = 0;
        verticalVelocity = 0;
    }

    public void Update() {
        move();
        // update animation
        super.Update();
    }

    private void move() {
        // left / right
        if (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) ^ Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT)) {
            for (int i = 0; i < MOVEMENTSPEED; i++) {
                float increment = (i == (int)MOVEMENTSPEED) ? (MOVEMENTSPEED - (int)MOVEMENTSPEED) : 1;
                increment *= Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) ? -1 : 1;
                position.x += increment;
                if ((Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) && (!currentLevel().IsEmptySpace(colBoxTopLeftPos()) || !currentLevel().IsEmptySpace(colBoxBottomLeftPos())))
                 || (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT) && (!currentLevel().IsEmptySpace(colBoxTopRightPos()) || !currentLevel().IsEmptySpace(colBoxBottomRightPos())))) {
                    position.x -= increment;
                    break;
                }
            }
        }

        // still have to do inverted values
        // jumping / falling
        if (IsGrounded()) {
            if (Game.instance.GameWindow.GetKeyDown(GLFW_KEY_SPACE)) {
                verticalVelocity -= JUMPVELOCITY;
            }
        } else {
            verticalVelocity += DOWNWARDACCELERATION;
        }

        for (int i = 0; i < Math.abs(verticalVelocity); i++) { // no iterations will run if verticalVelocity is 0
            float increment = (i == (int)verticalVelocity) ? (verticalVelocity - (int)verticalVelocity) : 1;
            increment *= (verticalVelocity < 0) ? -1 : 1;
            position.y += increment;
            if ((verticalVelocity > 0 && (!currentLevel().IsEmptySpace(colBoxBottomLeftPos()) || !currentLevel().IsEmptySpace(colBoxBottomRightPos())))
             || (verticalVelocity < 0 && (!currentLevel().IsEmptySpace(colBoxTopLeftPos()) || !currentLevel().IsEmptySpace(colBoxTopRightPos())))) {
                position.y -= increment;
                verticalVelocity = 0;
                break;
            }
        }
    }

    // only checks bottom corners because nothing is thinner than L3M
    public boolean IsGrounded() {
        if (!inverted) {
            return !currentLevel().IsEmptySpace(colBoxBottomLeftPos().add(new Vector2(0, 1))) ||
                   !currentLevel().IsEmptySpace(colBoxBottomRightPos().add(new Vector2(0, 1)));
        } else {
            return !currentLevel().IsEmptySpace(colBoxTopLeftPos().add(new Vector2(0, -1))) ||
                   !currentLevel().IsEmptySpace(colBoxTopRightPos().add(new Vector2(0, -1)));
        }
    }

    public void Invert(boolean inverted) {
        if (this.inverted == inverted) return;

        this.inverted = inverted;
        if (!inverted) {
            data = defaultData;
            frameOffset = 0;
            position.y -= 32;
        } else {
            data = invertedData;
            frameOffset = 0; // change this
            position.y += 32;
        }
        collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
        collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
        spriteOffset = data.SpriteOffset != null ? data.SpriteOffset.clone() : new Vector2();
    }

    public void Reset() {
        Invert(false);
        super.Reset();
    }

    private Vector2 colBoxTopLeftPos() {
        return position.add(collisionBoxCornerA);
    }

    private Vector2 colBoxTopRightPos() {
        return position.add(new Vector2(collisionBoxCornerB.x, collisionBoxCornerA.y));
    }

    private Vector2 colBoxBottomLeftPos() {
        return position.add(new Vector2(collisionBoxCornerA.x, collisionBoxCornerB.y));
    }

    private Vector2 colBoxBottomRightPos() {
        return position.add(collisionBoxCornerB);
    }

    private Level currentLevel() {
        return Game.instance.GetCurrentLevel();
    }
}
