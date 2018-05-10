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
        frameOffset = 0;
        verticalVelocity = 0;
    }

    public void Update() {
        if (Game.instance.GameWindow.GetKeyDown(GLFW_KEY_F) && canFlip()) {
            Invert(!inverted);
        }
        move();

        // update animation
        sprite.currentFrame = frameOffset;

        super.Update();
    }

    private void move() {
        // left / right
        if (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) ^ Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT)) {
            for (int i = 0; i < MOVEMENTSPEED; i++) {
                float increment = (i == (int)MOVEMENTSPEED) ? (MOVEMENTSPEED - (int)MOVEMENTSPEED) : 1;
                increment *= Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) ? -1 : 1;
                position.x += increment;
                if ((Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT) && (!isEmptySpace(colBoxTopLeftPos()) || !isEmptySpace(colBoxBottomLeftPos())))
                 || (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT) && (!isEmptySpace(colBoxTopRightPos()) || !isEmptySpace(colBoxBottomRightPos())))) {
                    position.x -= increment;
                    break;
                }
            }
        }

        // jumping / falling
        if (isGrounded()) {
            if (Game.instance.GameWindow.GetKeyDown(GLFW_KEY_SPACE)) {
                verticalVelocity -= JUMPVELOCITY * (inverted ? -1 : 1);
            }
        } else {
            verticalVelocity += DOWNWARDACCELERATION * (inverted ? -1 : 1);
        }

        for (int i = 0; i < Math.abs(verticalVelocity); i++) { // no iterations will run if verticalVelocity is 0
            float increment = (i == (int)verticalVelocity) ? (verticalVelocity - (int)verticalVelocity) : 1;
            increment *= (verticalVelocity < 0) ? -1 : 1;
            position.y += increment;
            if ((verticalVelocity > 0 && (!isEmptySpace(colBoxBottomLeftPos()) || !isEmptySpace(colBoxBottomRightPos())))
             || (verticalVelocity < 0 && (!isEmptySpace(colBoxTopLeftPos()) || !isEmptySpace(colBoxTopRightPos())))) {
                position.y -= increment;
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
            return currentLevel().GetSpaceType(colBoxBottomLeftPos().add(new Vector2(0, 1))) != Level.SPACE_WHITE ||
                   currentLevel().GetSpaceType(colBoxBottomRightPos().add(new Vector2(0, 1))) != Level.SPACE_WHITE;
        } else {
            return currentLevel().GetSpaceType(colBoxTopLeftPos().add(new Vector2(0, -1))) != Level.SPACE_BLACK ||
                   currentLevel().GetSpaceType(colBoxTopRightPos().add(new Vector2(0, -1))) != Level.SPACE_BLACK;
        }
    }

    private boolean canFlip() {
        if (!inverted) {
            return currentLevel().GetSpaceType(colBoxBottomLeftPos().add(new Vector2(0, 1))) == Level.SPACE_BLACK &&
                   currentLevel().GetSpaceType(colBoxBottomRightPos().add(new Vector2(0, 1))) == Level.SPACE_BLACK;
        } else {
            return currentLevel().GetSpaceType(colBoxTopLeftPos().add(new Vector2(0, -1))) == Level.SPACE_WHITE &&
                   currentLevel().GetSpaceType(colBoxTopRightPos().add(new Vector2(0, -1))) == Level.SPACE_WHITE;
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
            frameOffset = 8;
            position.y += 32;
        }
        collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
        collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
        spriteOffset = data.SpriteOffset != null ? data.SpriteOffset.clone() : new Vector2();
        currentLevel().Invert(inverted);
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
