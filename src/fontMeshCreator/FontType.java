package fontMeshCreator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import TTL.Sprite.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Represents a font. It holds the font's texture atlas as well as having the
 * ability to create the quad vertices for any text using this font.
 * 
 * @author Karl
 *
 */
public class FontType {

	private int textureAtlas;
	private TextMeshCreator loader;
	private int width;
	private int height;

	/**
	 * Creates a new font and loads up the data about each character from the
	 * font file.
	 * 
	 * @param fontImage
	 *            - the image file of the font
	 * @param fontFile
	 *            - the font file containing information about each character in
	 *            the texture atlas.
	 */
	public FontType(InputStream fontImage, InputStream fontFile) {
		try {
			BufferedImage bi = ImageIO.read(fontImage);
			width = bi.getWidth();
			height = bi.getHeight();
			applyPixels(Image.createPixelBuffer(bi));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.loader = new TextMeshCreator(fontFile);
	}

	/**
	 * @return The font texture atlas.
	 */
	public int getTextureAtlas() {
		return textureAtlas;
	}

	/**
	 * Takes in an unloaded text and calculate all of the vertices for the quads
	 * on which this text will be rendered. The vertex positions and texture
	 * coords and calculated based on the information from the font file.
	 * 
	 * @param text
	 *            - the unloaded text.
	 * @return Information about the vertices of all the quads.
	 */
	public TextMeshData loadText(Text text) {
		return loader.createTextMesh(text);
	}

	private void applyPixels(ByteBuffer pixels) {
		textureAtlas = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureAtlas);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
	}

}
