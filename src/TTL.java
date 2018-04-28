import java.io.FileNotFoundException;

public class TTL {
	public static TTL instance; // when creating a new TTL, remember to set this static instance

	public Display GameWindow;

	private Image circle;

	public TTL(int numPlayers, int numEpidemics) throws FileNotFoundException{

		GameWindow = new Display(this);
		GameWindow.addImage(new Image("./res/pandemicmap.png", 0, 0, 1016, 720));

		Image playerImage1 = new Image("./res/1.png", 320, 180, 32, 32);
		GameWindow.addImage(playerImage1);

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
