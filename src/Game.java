import java.io.FileNotFoundException;
import java.util.*;

public class Game {
	public static Game instance;

	public Display GameWindow;
	public HashMap<String, Level> Levels;

	private Level currentLevel;
	//private Player player;
	private ArrayList<GameObject> gameObjects; // does NOT include the Player GameObject

	public Game() throws FileNotFoundException{
		Game.instance = this;
		GameWindow = new Display(this);
		GameWindow.setIcon(new Image(new ImageData("./res/icon.png")));

		Levels = new HashMap<String, Level>();
		Levels.put("title", new LevelTitle());
//		Levels.put("levelselect", new LevelSelect());
//		Levels.put("1-1", new Level1x1());
//		Levels.put("1-2", new Level1x2());
//		Levels.put("1-3", new Level1x3());
//		Levels.put("1-4", new Level1x4());
//		Levels.put("1-5", new Level1x5());
//		Levels.put("2-1", new Level2x1());
//		Levels.put("2-2", new Level2x2());
//		Levels.put("2-3", new Level2x3());
//		Levels.put("2-4", new Level2x4());
//		Levels.put("2-5", new Level2x5());
//		Levels.put("3-1", new Level3x1());
//		Levels.put("3-2", new Level3x2());
//		Levels.put("3-3", new Level3x3());
//		Levels.put("3-4", new Level3x4());
//		Levels.put("3-5", new Level3x5());
//		Levels.put("credits", new LevelCredits());

		gameObjects = new ArrayList<GameObject>();
		currentLevel = Levels.get("title");
		currentLevel.Load();

		//setup player
		//player = new Player(new Image("./res/spr_char_standing_0.png"), new Vector2(32, 320));
		//GameWindow.addImage(player);
		//player.visible = false;

		GameWindow.run();
	}

	public static void main(String[] args) throws FileNotFoundException{
		new Game();
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

	public void AddGameObject(GameObject gameObject) {
		gameObjects.add(gameObject);
		GameWindow.addImage(gameObject.GetSprite(), 1);
	}

	public void DestroyGameObject(GameObject gameObject) {
		gameObjects.remove(gameObject);
		GameWindow.removeImage(gameObject.GetSprite());
	}

	public void DestroyAllGameObjects() {
		for (GameObject gameObject : gameObjects) {
			GameWindow.removeImage(gameObject.GetSprite());
		}
		gameObjects.clear();
	}

}
