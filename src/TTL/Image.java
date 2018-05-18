package TTL;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.nio.*;
import org.lwjgl.*;

import static org.lwjgl.opengl.GL11.*;

public class Image extends Sprite {
	public float Width;
	public float Height;
	public int CurrentFrame = 0;
	public boolean VerticalMirror = false;
	public boolean HorizontalMirror = false;

	protected int actualWidth;
	protected int actualHeight;

	private ImageData data;
	private int id;
	private int numFrames = 1;
	private int numSSColumns = 1;

	protected Image() {}

	public Image(ImageData data) {
		this.data = data;
		Init();
		Width = (data.Width == -1) ? (actualWidth / numSSColumns) : data.Width;
		Height = (data.Height == -1) ? (actualHeight / numSSRows()) : data.Height;
		this.numFrames = data.NumFrames;
		this.numSSColumns = data.NumSSColumns;
		layer = data.Layer;
	}

	protected void Init() {
		try {
			BufferedImage bi = ImageIO.read(new File(data.Filename));
			actualWidth = bi.getWidth();
			actualHeight = bi.getHeight();
			applyPixels(createPixelBuffer(bi));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ByteBuffer createPixelBuffer(BufferedImage bi) {
		int w = bi.getWidth(), h = bi.getHeight();
		int[] pixels_raw = bi.getRGB(0, 0, w, h, null, 0, w);
		ByteBuffer pixels = BufferUtils.createByteBuffer(w * h * 4);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int pixel = pixels_raw[y * w + x];
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
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, actualWidth, actualHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
	}

	public void Render() {
		if (!Visible) return;

		int windowWidth = Game.instance.GameWindow.GetWidth();
		int windowHeight = Game.instance.GameWindow.GetHeight();
		float texLeft = (float)(CurrentFrame % numSSColumns + (HorizontalMirror ? 1 : 0)) / numSSColumns;
		float texRight = (float)(CurrentFrame % numSSColumns + (HorizontalMirror ? 0 : 1)) / numSSColumns;
		float texTop = (float)(CurrentFrame / numSSColumns + (VerticalMirror ? 1 : 0)) / numSSRows();
		float texBot = (float)(CurrentFrame / numSSColumns + (VerticalMirror ? 0 : 1)) / numSSRows();
		float vertLeft = (Position.x / windowWidth * 2) - 1;
		float vertRight = ((Position.x + Width) / windowWidth * 2) - 1;
		float vertTop = -(Position.y / windowHeight * 2) + 1;
		float vertBot = -((Position.y + Height) / windowHeight * 2) + 1;


		glBindTexture(GL_TEXTURE_2D, id);
		glBegin(GL_QUADS);
		{
			glTexCoord2f(texLeft, texTop);
			glVertex2f(vertLeft, vertTop);
			glTexCoord2f(texLeft, texBot);
			glVertex2f(vertLeft, vertBot);
			glTexCoord2f(texRight, texBot);
			glVertex2f(vertRight, vertBot);
			glTexCoord2f(texRight, texTop);
			glVertex2f(vertRight, vertTop);
		}
		glEnd();
	}

	public int GetNumFrames() {
		return numFrames;
	}

	public void SetState(String state) {
		HorizontalMirror = state.charAt(0) == '-'; // ideally I would put + for false, but anything else actually works
		VerticalMirror = state.charAt(1) == '-';
		CurrentFrame = Integer.parseInt(state.substring(2));
	}

	public String GetState() {
		return "" + (HorizontalMirror ? '-' : '+') + (VerticalMirror ? '-' : '+') + CurrentFrame;
	}

	public void Reset() {
		if (data != null) {
			Width = (data.Width == -1) ? (actualWidth / numSSColumns) : data.Width;
			Height = (data.Height == -1) ? (actualHeight / numSSRows()) : data.Height;
		}
		CurrentFrame = 0;
		HorizontalMirror = false;
		VerticalMirror = false;
	}

	private int numSSRows() {
		return (numFrames + numSSColumns - 1) / numSSColumns;
	}
}
