package TTL.Sprite;

import TTL.*;

import static org.lwjgl.opengl.GL11.*;

public class BackgroundImage extends Image {
    private int key;

    public BackgroundImage(String filename, int key) {
        this.key = key;
        Init(filename);
        this.Width = actualWidth;
        this.Height = actualHeight;
        Position = new Vector2();
        layer = 0;
    }

    @Override
    public void Render() {
        if (!Visible) return;

        int[][] LevelMap = Game.instance.CurrentLevelMap;
        glBindTexture(GL_TEXTURE_2D, id);
        glBegin(GL_QUADS);
        for (float y = 0; y < LevelMap.length; y++){
            for (float x = 0; x < LevelMap[0].length; x++) {
                if (LevelMap[(int)y][(int)x] != key) continue;

                float texLeft  = x     / LevelMap[0].length;
                float texRight = (x+1) / LevelMap[0].length;
                float texTop   = y     / LevelMap.length;
                float texBot   = (y+1) / LevelMap.length;

                float vertLeft  =  (x     / LevelMap[0].length * 2) - 1;
                float vertRight =  ((x+1) / LevelMap[0].length * 2) - 1;
                float vertTop   = -(y     / LevelMap.length    * 2) + 1;
                float vertBot   = -((y+1) / LevelMap.length    * 2) + 1;
                if (HorizontalMirror) {
                    vertLeft  = 1 - vertLeft;
                    vertRight = 1 - vertRight;
                }
                if (VerticalMirror) {
                    vertTop = 1 - vertTop;
                    vertBot = 1 - vertBot;
                }

                glTexCoord2f(texLeft, texTop);  glVertex2f(vertLeft, vertTop);
                glTexCoord2f(texLeft, texBot);  glVertex2f(vertLeft, vertBot);
                glTexCoord2f(texRight, texBot); glVertex2f(vertRight, vertBot);
                glTexCoord2f(texRight, texTop); glVertex2f(vertRight, vertTop);
            }
        }
        glEnd();
    }
}
