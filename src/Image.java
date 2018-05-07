import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.nio.*;
import org.lwjgl.*;

import static org.lwjgl.opengl.GL11.*;

class ImageData {
	public String Filename;
	public float Width = -1; // -1 will use actual resolution
	public float Height = -1;
	public int NumFrames = 1;
	public int NumSSColumns = 1; // number of sprite sheet columns (don't really need to know the number of rows)
	public int Layer = 0;

	public ImageData(String filename) {
		Filename = filename;
	}
}

public class Image {
	public boolean visible = true;
	public boolean addedToDisplay = false;
	public float width;
	public float height;
	public Vector2 position;
	public int currentFrame = 0;

	protected String filename;
	protected int actualWidth;
	protected int actualHeight;
	protected ByteBuffer pixelBuffer;
	protected int layer;

	// private ImageData data; // no need to keep data
	private int id;
	private int numFrames = 1;
	private int numSSColumns = 1;

	protected Image() {}

	public Image(ImageData data) {
		// this.data = data; // no need to keep data
		filename = data.Filename;
		Init();
		width = data.Width;
		if (width == -1) {
			width = actualWidth / numSSColumns;
		}
		height = data.Height;
		if (height == -1) {
			height = actualHeight / numSSRows();
		}
		this.numFrames = data.NumFrames;
		this.numSSColumns = data.NumSSColumns;
		position = new Vector2();
		layer = data.Layer;
	}

	protected void Init() {
		try {
			BufferedImage bi = ImageIO.read(new File(filename));
			actualWidth = bi.getWidth();
			actualHeight = bi.getHeight();
			pixelBuffer = createPixelBuffer(bi);
			applyPixels(pixelBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		Bind();
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, actualWidth, actualHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
	}

	public void Bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public ByteBuffer GetPixelBuffer() {
		return pixelBuffer;
	}

	public int GetNumFrames() {
		return numFrames;
	}

	public int GetLayer() {
		return layer;
	}

	public float GetLeftSpriteCoord() {
		return (float)(currentFrame % numSSColumns) / numSSColumns;
	}

	public float GetRightSpriteCoord() {
		return (float)(currentFrame % numSSColumns + 1) / numSSColumns;
	}

	public float GetTopSpriteCoord() {
		return (float)(currentFrame / numSSColumns) / numSSRows();
	}

	public float GetBottomSpriteCoord() {
		return (float)(currentFrame / numSSColumns + 1) / numSSRows();
	}

	private int numSSRows() {
		return (numFrames + numSSColumns - 1) / numSSColumns;
	}
}
