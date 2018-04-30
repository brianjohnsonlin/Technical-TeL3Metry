import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.nio.*;
import org.lwjgl.*;

import static org.lwjgl.opengl.GL11.*;

public class Image {
	public boolean visible = true;
	public boolean addedToDisplay = false;

	public float width;
	public float height;
	public float x;
	public float y;

	protected String filename;
	protected int actualWidth;
	protected int actualHeight;

	private int id;

	public Image() {}

	public Image(String filename, float width, float height, float x, float y) {
		this.filename = filename;
		Init();
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}

	public Image(String filename, int x, int y) {
		this.filename = filename;
		Init();
		this.width = actualWidth;
		this.height = actualHeight;
		this.x = x;
		this.y = y;
	}

	public Image(String filename) {
		this.filename = filename;
		Init();
		this.width = actualWidth;
		this.height = actualHeight;
		x = y = 0;
	}

	protected void Init() {
		try {
			BufferedImage bi = ImageIO.read(new File(filename));
			actualWidth = bi.getWidth();
			actualHeight = bi.getHeight();
			ByteBuffer pixels = createPixelBuffer(bi);
			applyPixels(pixels);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	protected ByteBuffer createPixelBuffer(BufferedImage bi) {
		int[] pixels_raw = bi.getRGB(0, 0, actualWidth, actualHeight, null, 0, actualWidth);
		ByteBuffer pixels = BufferUtils.createByteBuffer(actualWidth * actualHeight * 4);
		for (int y = 0; y < actualHeight; y++) {
			for (int x = 0; x < actualWidth; x++) {
				int pixel = pixels_raw[y * actualWidth + x];
				pixels.put((byte)((pixel >> 16) & 0xFF));   //RED
				pixels.put((byte)((pixel >>  8) & 0xFF));   //GREEN
				pixels.put((byte)((pixel      ) & 0xFF));   //BLUE
				pixels.put((byte)((pixel >> 24) & 0xFF));   //ALPHA
			}
		}
		pixels.flip();
		return pixels;
	}

	protected void applyPixels(ByteBuffer pixels) {
		id = glGenTextures();
		bind();
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, actualWidth, actualHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
	}
}
