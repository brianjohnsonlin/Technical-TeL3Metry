import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BackgroundImage extends Image {
    private final int mapToImageScale = 32;

    private int key;

    public BackgroundImage(String filename, String levelMapFilename, Color key) {
        try {
            BufferedImage bi = ImageIO.read(new File(filename));
            BufferedImage levelMapBI = ImageIO.read(new File(levelMapFilename));
            width = actualWidth = bi.getWidth();
            height = actualHeight = bi.getHeight();
            x = y = 0;
            this.key = ColorToInt(key);

            ByteBuffer pixels = createPixelBufferWithMap(bi, levelMapBI);
            applyPixels(pixels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected ByteBuffer createPixelBufferWithMap(BufferedImage bi, BufferedImage levelMapBI) {
        int levelMapWidth = levelMapBI.getWidth();
        int levelMapHeight = levelMapBI.getHeight();
        int[] level_map = levelMapBI.getRGB(0, 0, levelMapWidth, levelMapHeight, null, 0, levelMapWidth);
        int[] pixels_raw = bi.getRGB(0, 0, actualWidth, actualHeight, null, 0, actualWidth);
        ByteBuffer pixels = BufferUtils.createByteBuffer(actualWidth * actualHeight * 4);
        for (int i = 0; i < actualHeight; i++) {
            for (int j = 0; j < actualWidth; j++) {
                int pixel = pixels_raw[i * actualWidth + j];
                pixels.put((byte)((pixel >> 16) & 0xFF));   //RED
                pixels.put((byte)((pixel >>  8) & 0xFF));   //GREEN
                pixels.put((byte)((pixel      ) & 0xFF));   //BLUE
                if (level_map[i / mapToImageScale * levelMapWidth + j / mapToImageScale] == key) { //ALPHA
                    pixels.put((byte)((pixel >> 24) & 0xFF));
                } else {
                    pixels.put((byte)0);
                }
            }
        }
        pixels.flip();
        return pixels;
    }

    private int ColorToInt(Color color){
        int r = (color.getRed() << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        int g = (color.getGreen() << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        int b = color.getBlue() & 0x000000FF; //Mask out anything not blue.
        int a = (color.getAlpha() << 24) & 0xFF000000; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
        return a | r | g | b;
    }
}
