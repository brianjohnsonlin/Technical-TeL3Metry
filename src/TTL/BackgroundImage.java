package TTL;

import TTL.Level.Level;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BackgroundImage extends Image {
    protected int[][] LevelMap;

    private int key;
    private String filename;

    public BackgroundImage(String filename, int[][] LevelMap, int key) {
        this.filename = filename;
        this.LevelMap = LevelMap;
        this.key = key;
        Init();
        this.Width = actualWidth;
        this.Height = actualHeight;
        Position = new Vector2(0, 0);
        layer = 0;
    }

    protected void Init() {
        try {
            BufferedImage bi = ImageIO.read(new File(filename));
            actualWidth = bi.getWidth();
            actualHeight = bi.getHeight();
            applyPixels(createPixelBufferWithMap(bi, LevelMap, key));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static ByteBuffer createPixelBufferWithMap(BufferedImage bi, int[][] levelMap, int key) {
        int w = bi.getWidth(), h = bi.getHeight();
        int[] pixels_raw = bi.getRGB(0, 0, w, h, null, 0, w);
        ByteBuffer pixels = BufferUtils.createByteBuffer(w * h * 4);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = pixels_raw[y * w + x];
                pixels.put((byte)((pixel >> 16) & 0xFF));   //RED
                pixels.put((byte)((pixel >>  8) & 0xFF));   //GREEN
                pixels.put((byte)((pixel      ) & 0xFF));   //BLUE
                if (levelMap[y/ Level.MAPTOIMAGESCALE][x/Level.MAPTOIMAGESCALE] == key) { //ALPHA
                    pixels.put((byte)((pixel >> 24) & 0xFF));
                } else {
                    pixels.put((byte)0);
                }
            }
        }
        pixels.flip();
        return pixels;
    }

    //private int ColorToInt(Color color){
    //    int r = (color.getRed() << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
    //    int g = (color.getGreen() << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
    //    int b = color.getBlue() & 0x000000FF; //Mask out anything not blue.
    //    int a = (color.getAlpha() << 24) & 0xFF000000; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    //    return a | r | g | b;
    //}
}
