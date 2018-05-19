package TTL.GameObject;

import TTL.*;
import TTL.Level.Level;

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
    private boolean facingLeft;

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
        facingLeft = false;
    }

    public void Update() {
        if (Game.instance.GameWindow.GetKeyDown(GLFW_KEY_F) && canFlip()) {
            Invert();
        }
        move();
        sprite.SetState("" + (facingLeft ? '-' : '+') + (inverted ? '-' : '+') + (stuck() ? 7 : (int)currentFrame + frameOffset));
        super.Update();
    }

    public void Invert(boolean inverted) {
        Game.instance.SetBackgroundsInverted(inverted);

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
    }

    public void Invert() {
        Invert(!inverted);
    }

    public void Reset() {
        verticalVelocity = 0;
        Invert(false);
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

    private boolean isEmptySpace(Vector2 coord) {
        return Game.instance.GetSpaceType(coord) == (inverted ? Level.SPACE_BLACK : Level.SPACE_WHITE);
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
