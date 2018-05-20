package TTL.GameObject;

import TTL.*;
import TTL.Level.Level;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends GameObject {
    private final float MOVEMENTSPEED = 5;
    private final float JUMPVELOCITY = 9f;
    private final float DOWNWARDACCELERATION = 1f;
    private final float FRAMESPEED = 0.25f;

    protected GameObjectData defaultData;
    protected GameObjectData invertedData;
    protected boolean inverted = false;
    protected int defaultFrameOffset;
    protected int invertedFrameOffset;

    private float currentFrame = 0;
    private int frameOffset = 0;
    private float verticalVelocity = 0;
    private boolean facingLeft = false;

    public Player() {
        ImageData spriteData = new ImageData("./res/spr_char_0.png"); {
            spriteData.Width = 32;
            spriteData.Height = 32;
            spriteData.NumFrames = 16;
            spriteData.NumSSColumns = 4;
            spriteData.Layer = 2;
        }

        defaultData = new GameObjectData(); {
            defaultData.CollisionBoxCornerA = new Vector2(4, 6);
            defaultData.CollisionBoxCornerB = new Vector2(27, 31);
            defaultData.SpriteData = spriteData;
        }

        invertedData = new GameObjectData(); {
            invertedData.CollisionBoxCornerA = new Vector2(4, 0);
            invertedData.CollisionBoxCornerB = new Vector2(27, 25);
            invertedData.SpriteData = spriteData;
        }

        data = defaultData;
        defaultFrameOffset = 0;
        invertedFrameOffset = 8;
        Init();
    }

    public void Update() {
        if (Game.instance.GameWindow.GetKeyDown(GLFW_KEY_F) && canFlip()) {
            Invert(!inverted);
            Game.instance.SetBackgroundsInverted(inverted);
        }
        move();
        sprite.SetState("" + (facingLeft ? '-' : '+') + (inverted ? '-' : '+') + (stuck() ? 7 : (int)currentFrame + frameOffset));
        super.Update();
    }

    public void Invert(boolean inverted) {
        if (this.inverted == inverted) return;

        this.inverted = inverted;
        if (!inverted) {
            data = defaultData;
            frameOffset = defaultFrameOffset;
            Position.y -= 32;
        } else {
            data = invertedData;
            frameOffset = invertedFrameOffset;
            Position.y += 32;
        }
        collisionBoxCornerA = data.CollisionBoxCornerA != null ? data.CollisionBoxCornerA.clone() : null;
        collisionBoxCornerB = data.CollisionBoxCornerB != null ? data.CollisionBoxCornerB.clone() : null;
        spriteOffset = data.SpriteOffset != null ? data.SpriteOffset.clone() : new Vector2();
    }

    public void Reset() {
        verticalVelocity = 0;
        Invert(Game.instance.GetCurrentLevel().GetStartInverted());
        currentFrame = 0;
        super.Reset();
        Position.replaceWith(Game.instance.GetCurrentLevel().GetStartingPoint());
        sprite.Reset();
    }

    private void move() {
        // left / right
        boolean left = Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT);
        boolean right = Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT);
        if (left ^ right && !stuck()) {
            for (int i = 0; i < MOVEMENTSPEED; i++) {
                float increment = (i == (int)MOVEMENTSPEED) ? (MOVEMENTSPEED - (int)MOVEMENTSPEED) : 1;
                increment *= left ? -1 : 1;
                Position.x += increment;
                if ((left && (!isEmptySpace(ColBoxTopLeftPos()) || !isEmptySpace(ColBoxBottomLeftPos())))
                 || (right && (!isEmptySpace(ColBoxTopRightPos()) || !isEmptySpace(ColBoxBottomRightPos())))) {
                    Position.x -= increment;
                    currentFrame = 0;
                    break;
                }
            }

            facingLeft = left;
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

    protected boolean isEmptySpace(Vector2 coord) {
        return Game.instance.GetSpaceType(coord) == (inverted ? Level.SPACE_BLACK : Level.SPACE_WHITE); // TODO: need to account for forcefields
    }

    // only checks bottom corners because nothing is thinner than L3M
    private boolean isGrounded() {
        if (!inverted) {
            return Game.instance.GetSpaceType(ColBoxBottomLeftPos().add(new Vector2(0, 1))) != Level.SPACE_WHITE ||
                   Game.instance.GetSpaceType(ColBoxBottomRightPos().add(new Vector2(0, 1))) != Level.SPACE_WHITE;
        } else {
            return Game.instance.GetSpaceType(ColBoxTopLeftPos().add(new Vector2(0, -1))) != Level.SPACE_BLACK ||
                   Game.instance.GetSpaceType(ColBoxTopRightPos().add(new Vector2(0, -1))) != Level.SPACE_BLACK;
        }
    }

    private boolean canFlip() {
        if (!inverted) {
            return Game.instance.GetSpaceType(ColBoxBottomLeftPos().add(new Vector2(0, 1))) == Level.SPACE_BLACK &&
                   Game.instance.GetSpaceType(ColBoxBottomRightPos().add(new Vector2(0, 1))) == Level.SPACE_BLACK;
        } else {
            return Game.instance.GetSpaceType(ColBoxTopLeftPos().add(new Vector2(0, -1))) == Level.SPACE_WHITE &&
                   Game.instance.GetSpaceType(ColBoxTopRightPos().add(new Vector2(0, -1))) == Level.SPACE_WHITE;
        }
    }

    private boolean stuck() {
        for (int y = (int)(collisionBoxCornerA.y + Position.y); y <= collisionBoxCornerB.y + Position.y; y++) {
            for (int x = (int)(collisionBoxCornerA.x + Position.x); x <= collisionBoxCornerB.x + Position.x; x++) {
                if (!isEmptySpace(new Vector2(x, y))) {
                    return true;
                }
            }
        }
        return false;
    }
}
