import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BackgroundImage extends Image {
    private final static int mapToImageScale = 32;

    protected int[][] LevelMap;

    private int key;

    public BackgroundImage(String filename, int[][] LevelMap, int key) {
        this.filename = filename;
        this.LevelMap = LevelMap;
        this.key = key;
        Init();
        this.width = actualWidth;
        this.height = actualHeight;
        position = new Vector2(0, 0);
        layer = 0;
    }

    protected void Init() {
        try {
            BufferedImage bi = ImageIO.read(new File(filename));
            actualWidth = bi.getWidth();
            actualHeight = bi.getHeight();
            pixelBuffer = createPixelBufferWithMap(bi, LevelMap);
            applyPixels(pixelBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected ByteBuffer createPixelBufferWithMap(BufferedImage bi, int[][] levelMap) {
        int[] pixels_raw = bi.getRGB(0, 0, actualWidth, actualHeight, null, 0, actualWidth);
        ByteBuffer pixels = BufferUtils.createByteBuffer(actualWidth * actualHeight * 4);
        for (int y = 0; y < actualHeight; y++) {
            for (int x = 0; x < actualWidth; x++) {
                int pixel = pixels_raw[y * actualWidth + x];
                pixels.put((byte)((pixel >> 16) & 0xFF));   //RED
                pixels.put((byte)((pixel >>  8) & 0xFF));   //GREEN
                pixels.put((byte)((pixel      ) & 0xFF));   //BLUE
                if (levelMap[y/mapToImageScale][x/mapToImageScale] == key) { //ALPHA
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
