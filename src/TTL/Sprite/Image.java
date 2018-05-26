package TTL.Sprite;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.nio.*;
import java.util.*;

import TTL.*;
import org.lwjgl.*;

import static org.lwjgl.opengl.GL11.*;

public class Image extends Sprite {
	private static HashMap<String, ImageLibraryEntry> imageLibrary = new HashMap<>();

	public float Width;
	public float Height;
	public int CurrentFrame = 0;
	public boolean VerticalMirror = false;
	public boolean HorizontalMirror = false;

	protected ImageData data;
	protected int id;
	protected int actualWidth;
	protected int actualHeight;
	protected int numFrames = 1;
	protected int numSSColumns = 1;

	protected Image() {}

	public Image(ImageData data) {
		this.data = data;
		Init(data.Filename);
		numFrames = data.NumFrames;
		numSSColumns = data.NumSSColumns < 0 ? data.NumFrames : data.NumSSColumns;
		Width = (data.Width == -1) ? (actualWidth / numSSColumns) : data.Width;
		Height = (data.Height == -1) ? (actualHeight / numSSRows()) : data.Height;
		layer = data.Layer;
	}

	protected void Init(String imageFilename) {
		ImageLibraryEntry entry = getImageLibraryEntry(imageFilename);
		actualWidth = entry.Width;
		actualHeight = entry.Height;
		id = entry.ID;
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

	public static void PreloadImages(String[] imageFilenames) {
		for (String imageFilename : imageFilenames) {
			getImageLibraryEntry(imageFilename);
		}
	}

	@Override
	public void Render() {
		if (!Visible) return;

		float texLeft  = (float)(CurrentFrame % numSSColumns + (HorizontalMirror ? 1 : 0)) / numSSColumns;
		float texRight = (float)(CurrentFrame % numSSColumns + (HorizontalMirror ? 0 : 1)) / numSSColumns;
		float texTop   = (float)(CurrentFrame / numSSColumns + (VerticalMirror ? 1 : 0)) / numSSRows();
		float texBot   = (float)(CurrentFrame / numSSColumns + (VerticalMirror ? 0 : 1)) / numSSRows();

		int windowWidth  = Game.instance.GameWindow.GetWidth();
		int windowHeight = Game.instance.GameWindow.GetHeight();
		float vertLeft  = (Position.x / windowWidth * 2) - 1;
		float vertRight = ((Position.x + Width) / windowWidth * 2) - 1;
		float vertTop   = -(Position.y / windowHeight * 2) + 1;
		float vertBot   = -((Position.y + Height) / windowHeight * 2) + 1;

		glBindTexture(GL_TEXTURE_2D, id);
		glBegin(GL_QUADS);
		{
			glTexCoord2f(texLeft, texTop);  glVertex2f(vertLeft, vertTop);
			glTexCoord2f(texLeft, texBot);  glVertex2f(vertLeft, vertBot);
			glTexCoord2f(texRight, texBot); glVertex2f(vertRight, vertBot);
			glTexCoord2f(texRight, texTop); glVertex2f(vertRight, vertTop);
		}
		glEnd();
	}

	public int GetNumFrames() {
		return numFrames;
	}

	@Override
	public void SetState(String state) {
		HorizontalMirror = state.charAt(0) == '-'; // ideally I would put + for false, but anything else actually works
		VerticalMirror = state.charAt(1) == '-';
		CurrentFrame = Integer.parseInt(state.substring(2));
	}

	@Override
	public String GetState() {
		return "" + (HorizontalMirror ? '-' : '+') + (VerticalMirror ? '-' : '+') + CurrentFrame;
	}

	@Override
	public void Reset() {
		if (data != null) {
			Width = (data.Width == -1) ? (actualWidth / numSSColumns) : data.Width;
			Height = (data.Height == -1) ? (actualHeight / numSSRows()) : data.Height;
		}
		CurrentFrame = 0;
		HorizontalMirror = false;
		VerticalMirror = false;
	}

	protected int numSSRows() {
		return (numFrames + numSSColumns - 1) / numSSColumns;
	}

	private static ImageLibraryEntry getImageLibraryEntry(String imageFilename) {
		ImageLibraryEntry entry = imageLibrary.get(imageFilename);
		if (entry == null) {
			try {
				BufferedImage bi = ImageIO.read(ClassLoader.getSystemResourceAsStream(imageFilename));
				int width = bi.getWidth(), height = bi.getHeight();
				int id = glGenTextures();
				glBindTexture(GL_TEXTURE_2D, id);
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, createPixelBuffer(bi));
				entry = new ImageLibraryEntry(id, width, height);
				imageLibrary.put(imageFilename, entry);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return entry;
	}
}
