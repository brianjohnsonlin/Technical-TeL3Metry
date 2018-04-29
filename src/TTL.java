import java.awt.*;
import java.io.FileNotFoundException;

public class TTL {
	public static TTL instance; // when creating a new TTL, remember to set this static instance

	public Display GameWindow;

	private Level currentLevel;
	private Image player;

	public TTL() throws FileNotFoundException{

		GameWindow = new Display(this);
		GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_gear.png", "./res/levels/title.png", Color.white));
		GameWindow.addImage(new BackgroundImage("./res/backgrounds/bkg_blue.png", "./res/levels/title.png", Color.black));

		player = new Image("./res/spr_char_standing_0.png", 32, 320, 32, 32);
		GameWindow.addImage(player);
		//player.visible = false;

		GameWindow.run();
	}

	public void Update() {
		// selecting image to drag
		if (GameWindow.GetLeftMouseDown()) {
//			for (Image img : selectables) {
//				if (GameWindow.GetMouseOver(img)) {
//					selectedObject = img;
//					selectedObjectOffsetX = (float)GameWindow.GetMouseX() - selectedObject.x;
//					selectedObjectOffsetY = (float)GameWindow.GetMouseY() - selectedObject.y;
//					break;
//				}
//			}
		}

		// unselecting image
		if (GameWindow.GetLeftMouseUp()) {
			//selectedObject = null;
		}

		// dragging image
//		if (GameWindow.GetLeftMouseHeld() && selectedObject != null) {
//			selectedObject.x = (float)GameWindow.GetMouseX() - selectedObjectOffsetX;
//			selectedObject.y = (float)GameWindow.GetMouseY() - selectedObjectOffsetY;
//		}

		// hovering cities
//		boolean overCity = false;
//		ArrayList<City> cities = getCities();
//		for (City city : cities) {
//			if (GameWindow.GetMouseOver(city.getX() * 72/85, city.getY() * 72/85, 9)) {
//				circle.visible = true;
//				circle.x = city.getX() * 72/85 - 9;
//				circle.y = city.getY() * 72/85 - 9;
//				overCity = true;
//				break;
//			}
//		}
//
//		if (!overCity) {
//			circle.visible = false;
//		}
	}

	public static void main(String[] args) throws FileNotFoundException{
		TTL.instance = new TTL();
	}

}
