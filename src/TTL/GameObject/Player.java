package TTL.GameObject;

import TTL.*;
import TTL.Level.Level;
import TTL.Sprite.*;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends GameObject {
    private final float MOVEMENTSPEED = 5;
    private final float JUMPVELOCITY = 9f;
    private final float DOWNWARDACCELERATION = 1f;

    private final float FRAMESPEED = 0.25f;
    private final int STANDINGFRAME = 0;
    private final int FIRSTWALKFRAME = 0;
    private final int NUMWALKFRAMES = 6;
    private final int FALLINGFRAME = 6;
    private final int STUCKFRAME = 7;

    protected GameObjectData defaultData;
    protected GameObjectData invertedData;
    protected boolean inverted = false;
    protected int defaultFrameOffset;
    protected int invertedFrameOffset;

    protected float currentFrame = STANDINGFRAME;
    protected int frameOffset = 0;
    protected float verticalVelocity = 0;
    protected boolean facingLeft = false;

    private Vector2 startRecordingPosition;
    private LinkedList<Boolean> recordedInputs;
    private boolean recordedStartInverted;
    private PlayerDuplicate duplicate;
    private Image recordingSymbol;

    public Player() {}

    public Player(boolean startInverted) {
        ImageData spriteData = new ImageData("./res/spr_char_0.png"); {
            spriteData.Width = 32;
            spriteData.Height = 32;
            spriteData.NumFrames = 16;
            spriteData.NumSSColumns = 4;
            spriteData.Layer = 3;
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

        data = startInverted ? invertedData : defaultData;
        defaultFrameOffset = 0;
        invertedFrameOffset = 8;
        Init();

        startRecordingPosition = null;
        recordedInputs = null;
        duplicate = new PlayerDuplicate(startInverted);
        Game.instance.GameWindow.addSprite(duplicate.GetSprite());

        // initialize the recording dot symbol
        ImageData recData = new ImageData("./res/spr_record_0.png"); {
            recData.Layer = 3;
        }
        recordingSymbol = new Image(recData);
        recordingSymbol.Visible = false;
        Game.instance.GameWindow.addSprite(recordingSymbol);
    }

    @Override
    public void Update() {
        boolean f = Game.instance.GameWindow.GetKeyDown(GLFW_KEY_F);
        boolean left = Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_LEFT);
        boolean right = Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_RIGHT);
        boolean space = Game.instance.GameWindow.GetKeyDown(GLFW_KEY_SPACE);

        // recording ability
        if (startRecordingPosition == null) {
            if (Game.instance.GameWindow.GetKeyDown(GLFW_KEY_D)) {
                startRecordingPosition = Position.clone();
                recordedStartInverted = inverted;
                recordedInputs = new LinkedList<>();
            }
            recordingSymbol.Visible = false;
        }

        if (startRecordingPosition != null){
            if (Game.instance.GameWindow.GetKeyHeld(GLFW_KEY_D)) {
                recordingSymbol.Visible = true;
                recordedInputs.add(f);
                recordedInputs.add(left);
                recordedInputs.add(right);
                recordedInputs.add(space);
            } else {
                recordingSymbol.Visible = false;
                duplicate.Execute(startRecordingPosition, recordedStartInverted, recordedInputs);
                Invert(recordedStartInverted);
                Game.instance.SetBackgroundsInverted(inverted);
                Position.replaceWith(startRecordingPosition);
                startRecordingPosition = null;
                recordedInputs = null;
            }
        }

        if (f && canFlip()) {
            Game.instance.SetBackgroundsInverted(!inverted);
        }

        move(f, left, right, space);
        updateSprite();
        recordingSymbol.Position = Position.add(new Vector2(25, inverted ? 25 : 0));
        duplicate.Update();
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

    @Override
    public void Reset() {
        verticalVelocity = 0;
        Invert(Game.instance.GetCurrentLevel().GetStartInverted());
        currentFrame = STANDINGFRAME;
        spriteOffset.replaceWith(data.SpriteOffset != null ? data.SpriteOffset : new Vector2()); // probably unnecessary
        Position.replaceWith(Game.instance.GetCurrentLevel().GetStartingPoint());
        sprite.Reset();
        updateSprite();

        // reset recoding ability stuff
        startRecordingPosition = null;
        recordedInputs = null;
        duplicate.Reset();
        recordingSymbol.Visible = false;
    }

    public PlayerDuplicate getDuplicate() {
        return duplicate;
    }

    protected void move(boolean flip, boolean left, boolean right, boolean space) {
        // flip
        if (flip && canFlip()) {
            Invert(!inverted);
        }

        // left / right
        if (left ^ right && !stuck()) {
            for (int i = 0; i < MOVEMENTSPEED; i++) {
                float increment = (i == (int)MOVEMENTSPEED) ? (MOVEMENTSPEED - (int)MOVEMENTSPEED) : 1;
                increment *= left ? -1 : 1;
                Position.x += increment;
                if ((left && (!isEmptySpace(ColBoxTopLeftPos()) || !isEmptySpace(ColBoxBottomLeftPos())))
                 || (right && (!isEmptySpace(ColBoxTopRightPos()) || !isEmptySpace(ColBoxBottomRightPos())))) {
                    Position.x -= increment;
                    currentFrame = STANDINGFRAME;
                    break;
                }
            }

            facingLeft = left;
            currentFrame = FIRSTWALKFRAME + ((currentFrame + FRAMESPEED) % NUMWALKFRAMES);
        } else {
            currentFrame = STANDINGFRAME;
        }

        // jumping / falling
        if (isGrounded()) {
            if (space) {
                verticalVelocity -= JUMPVELOCITY * (inverted ? -1 : 1);
            }
        } else {
            verticalVelocity += DOWNWARDACCELERATION * (inverted ? -1 : 1);
            currentFrame = FALLINGFRAME;
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

    protected void updateSprite() {
        sprite.SetState("" + (facingLeft ? '-' : '+') + (inverted ? '-' : '+') + (stuck() ? STUCKFRAME : (int)currentFrame + frameOffset));
        sprite.Position = Position.add(spriteOffset);
    }

    protected boolean isEmptySpace(Vector2 coord) {
        if (Game.instance.GetSpaceType(coord) != (inverted ? Level.SPACE_BLACK : Level.SPACE_WHITE)) {
            return false; // if not empty space
        }

        if (Game.instance.ForcefieldMap[Game.instance.LocationToCoordinatesY(coord)][Game.instance.LocationToCoordinatesX(coord)]) {
            return false; // if occupied by forcefield
        }

        return true;
    }

    // only checks bottom corners because nothing is thinner than L3M
    private boolean isGrounded() {
        if (!inverted) {
            return !isEmptySpace(ColBoxBottomLeftPos().add(new Vector2(0, 1))) ||
                   !isEmptySpace(ColBoxBottomRightPos().add(new Vector2(0, 1)));
        } else {
            return !isEmptySpace(ColBoxTopLeftPos().add(new Vector2(0, -1))) ||
                   !isEmptySpace(ColBoxTopRightPos().add(new Vector2(0, -1)));
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
