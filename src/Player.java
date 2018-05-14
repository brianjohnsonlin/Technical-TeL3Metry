import static org.lwjgl.glfw.GLFW.*;

public class Player extends GameObject {
    private final float MOVEMENTSPEED = 5;
    private final float JUMPVELOCITY = 9f;
    private final float DOWNWARDACCELERATION = 1f;
    private final float FRAMESPEED = 0.25f;

    private GameObjectData defaultData;
    private GameObjectData invertedData;
    private boolean inverted;
    private float currentFrame;
    private int frameOffset;
    private float verticalVelocity;

    public Player() {
        ImageData sprite = new ImageData("./res/spr_char_0.png"); {
            sprite.Width = 32;
            sprite.Height = 32;
            sprite.NumFrames = 16;
            sprite.NumSSColumns = 4;
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
        currentFrame = 0;
        frameOffset = 0;
        verticalVelocity = 0;
    }

    public void Update() {
        if (Game.instance.GameWindow.GetKeyDown(GLFW_KEY_F) && canFlip()) {
            Invert();
        }
        move();

        // if stuck, change to stuck frame
        sprite.CurrentFrame = (int)currentFrame + frameOffset;

        super.Update();
    }

    private void move() {
        // left / right
        if (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) ^ Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT)) {
            for (int i = 0; i < MOVEMENTSPEED; i++) {
                float increment = (i == (int)MOVEMENTSPEED) ? (MOVEMENTSPEED - (int)MOVEMENTSPEED) : 1;
                increment *= Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) ? -1 : 1;
                Position.x += increment;
                if ((Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) && (!isEmptySpace(ColBoxTopLeftPos()) || !isEmptySpace(ColBoxBottomLeftPos())))
                 || (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT) && (!isEmptySpace(ColBoxTopRightPos()) || !isEmptySpace(ColBoxBottomRightPos())))) {
                    Position.x -= increment;
                    currentFrame = 0;
                    break;
                }
            }

            sprite.HorizontalMirror = Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT);
            currentFrame += FRAMESPEED;
            currentFrame %= 6;
        } else {
            currentFrame = 0;
        }

        // jumping / falling
        if (isGrounded()) {
            if (Game.instance.GameWindow.GetKeyDown(GLFW_KEY_SPACE)) {
                verticalVelocity -= JUMPVELOCITY * (inverted ? -1 : 1);
            }
        } else {
            verticalVelocity += DOWNWARDACCELERATION * (inverted ? -1 : 1);
            currentFrame = 6;
        }

        for (int i = 0; i < Math.abs(verticalVelocity); i++) { // no iterations will run if verticalVelocity is 0
            float increment = (i == (int)verticalVelocity) ? (verticalVelocity - (int)verticalVelocity) : 1;
            increment *= (verticalVelocity < 0) ? -1 : 1;
            Position.y += increment;
            if ((verticalVelocity > 0 && (!isEmptySpace(ColBoxBottomLeftPos()) || !isEmptySpace(ColBoxBottomRightPos())))
             || (verticalVelocity < 0 && (!isEmptySpace(ColBoxTopLeftPos()) || !isEmptySpace(ColBoxTopRightPos())))) {
                Position.y -= increment;
                verticalVelocity = 0;
                break;
            }
        }
    }

    private boolean isEmptySpace(Vector2 coord) {
        return currentLevel().GetSpaceType(coord) == (inverted ? Level.SPACE_BLACK : Level.SPACE_WHITE);
    }

    // only checks bottom corners because nothing is thinner than L3M
    private boolean isGrounded() {
        if (!inverted) {
            return currentLevel().GetSpaceType(ColBoxBottomLeftPos().add(new Vector2(0, 1))) != Level.SPACE_WHITE ||
                   currentLevel().GetSpaceType(ColBoxBottomRightPos().add(new Vector2(0, 1))) != Level.SPACE_WHITE;
        } else {
            return currentLevel().GetSpaceType(ColBoxTopLeftPos().add(new Vector2(0, -1))) != Level.SPACE_BLACK ||
                   currentLevel().GetSpaceType(ColBoxTopRightPos().add(new Vector2(0, -1))) != Level.SPACE_BLACK;
        }
    }

    private boolean canFlip() {
        if (!inverted) {
            return currentLevel().GetSpaceType(ColBoxBottomLeftPos().add(new Vector2(0, 1))) == Level.SPACE_BLACK &&
                   currentLevel().GetSpaceType(ColBoxBottomRightPos().add(new Vector2(0, 1))) == Level.SPACE_BLACK;
        } else {
            return currentLevel().GetSpaceType(ColBoxTopLeftPos().add(new Vector2(0, -1))) == Level.SPACE_WHITE &&
                   currentLevel().GetSpaceType(ColBoxTopRightPos().add(new Vector2(0, -1))) == Level.SPACE_WHITE;
        }
    }

    public void Invert(boolean inverted) {
        if (this.inverted == inverted) return;

        this.inverted = inverted;
        if (!inverted) {
            data = defaultData;
            frameOffset = 0;
            Position.y -= 32;
        } else {
            data = invertedData;
            frameOffset = 8;
            Position.y += 32;
        }
        collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
        collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
        spriteOffset = data.SpriteOffset != null ? data.SpriteOffset.clone() : new Vector2();
        currentLevel().Invert(inverted);
    }

    public void Invert() {
        Invert(!inverted);
    }

    public void Reset() {
        verticalVelocity = 0;
        Invert(false);
        currentFrame = 0;
        super.Reset();
        Position.replaceWith(currentLevel().GetStartingPoint());
        sprite.Reset();
    }

    private Level currentLevel() {
        return Game.instance.GetCurrentLevel();
    }
}
