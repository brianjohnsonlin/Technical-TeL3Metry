import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.nio.*;
import org.lwjgl.*;

import static org.lwjgl.opengl.GL11.*;

public class Image {
	public boolean visible = true;
	public boolean addedToDisplay = false;

	public float x;
	public float y;
	public float width;
	public float height;

	private int id;
	private int actualWidth;
	private int actualHeight;

	public Image(String filename, float x, float y, float width, float height) {
		this(filename);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Image(String filename) {
		BufferedImage bi;
		try {
			bi = ImageIO.read(new File(filename));
			actualWidth = bi.getWidth();
			actualHeight = bi.getHeight();

			int[] pixels_raw;
			pixels_raw = bi.getRGB(0, 0, actualWidth, actualHeight, null, 0, actualWidth);
			ByteBuffer pixels = BufferUtils.createByteBuffer(actualWidth * actualHeight * 4);
			for (int i = 0; i < actualHeight; i++) {
				for (int j = 0; j < actualWidth; j++) {
					int pixel = pixels_raw[i * actualWidth + j];
					pixels.put((byte)((pixel >> 16) & 0xFF));   //RED
					pixels.put((byte)((pixel >>  8) & 0xFF));   //GREEN
					pixels.put((byte)((pixel      ) & 0xFF));   //BLUE
					//pixels.put((byte)((pixel >> 24) & 0xFF));   //ALPHA
					pixels.put((byte)0);   //ALPHA
				}
			}
			pixels.flip();

			id = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, actualWidth, actualHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
}
