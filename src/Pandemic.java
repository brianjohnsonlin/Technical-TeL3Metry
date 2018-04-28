import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import org.json.*;

public class Pandemic {
	public static Pandemic instance; // when creating a new Pandemic, remember to set this static instance

	public Display GameWindow;

	private Image circle;

	public Pandemic(int numPlayers, int numEpidemics) throws FileNotFoundException{

		GameWindow = new Display(this);
		GameWindow.addImage(new Image("./res/pandemicmap.png", 0, 0, 1016, 720));

		Image playerImage1 = new Image("./res/1.png", 320, 180, 32, 32);
		Image playerImage2 = new Image("./res/2.png", 320, 540, 32, 32);
		Image playerImage3 = new Image("./res/3.png", 960, 180, 32, 32);
		Image playerImage4 = new Image("./res/4.png", 960, 540, 32, 32);
		GameWindow.addImage(playerImage1);
		GameWindow.addImage(playerImage2);
		GameWindow.addImage(playerImage3);
		GameWindow.addImage(playerImage4);

		circle = new Image("./res/circle.png", 0, 0, 18, 18);
		GameWindow.addImage(circle);
		circle.visible = false;

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


}
