package TTL;

import java.awt.Color;
import fontMeshCreator.*;

import static org.lwjgl.opengl.GL11.*;

public class Text extends Sprite{
	private TextData data;
	private String textString;
	private float fontSize;
	private Color color;
	private float lineMaxSize;
	private int numberOfLines;
	private FontType font;
	private boolean centerText;
	private TextMeshData meshData = null; // reset to null whenever textString is changed so it re-renders

	public Text(TextData data) {
		this.data = data;
		this.textString = data.Text;
		this.fontSize = data.FontSize;
		this.font = Game.instance.GetFont(data.FontName);
		this.color = data.Color;
		this.lineMaxSize = data.MaxLineLength;
		this.centerText = data.Centered;
		this.layer = data.Layer;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setNumberOfLines(int number) {
		this.numberOfLines = number;
	}

	public boolean isCentered() {
		return centerText;
	}

	public float getMaxLineSize() {
		return lineMaxSize;
	}

	public String getTextString() {
		return textString;
	}

	public void setTextString(String newText) {
		textString = newText;
		meshData = null;
	}

	@Override
	public void SetState(String state) {
		setTextString(state);
	}

	@Override
	public String GetState() {
		return textString;
	}

	@Override
	public void Render() {
		// generate meshData if it's null
		if (meshData == null) {
			meshData = font.loadText(this);
		}

		int windowWidth = Game.instance.GameWindow.GetWidth();
		int windowHeight = Game.instance.GameWindow.GetHeight();

		glBindTexture(GL_TEXTURE_2D, font.getTextureAtlas());
		glBegin(GL_TRIANGLES);
		{
			float r = color.getRed() / 255f;
			float g = color.getGreen() / 255f;
			float b = color.getBlue() / 255f;
			glColor3f(r, g ,b);
			for (int i = 0; i < meshData.getVertexCount(); i++) {
				float[] textCoords = meshData.getTextureCoords();
				float[] vertextPos = meshData.getVertexPositions();
				glTexCoord2f(textCoords[i*2], textCoords[i*2+1]);
				glVertex2f(vertextPos[i*2] + (Position.x / windowWidth * 2), vertextPos[i*2+1] - (Position.y / windowHeight * 2));
			}
			glColor3f(1.0f, 1.0f, 1.0f); // reset to white
		}
		glEnd();
	}

	@Override
	public void Reset() {
		setTextString(data.Text);
		this.fontSize = data.FontSize;
		this.font = Game.instance.GetFont(data.FontName);
		this.color = data.Color;
		this.lineMaxSize = data.MaxLineLength;
		this.centerText = data.Centered;
	}

}
