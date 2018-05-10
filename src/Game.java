import java.io.FileNotFoundException;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
	public static Game instance;

	public Display GameWindow;
	public HashMap<String, Level> Levels;

	private Level currentLevel;
	private Player player;
	private ArrayList<GameObject> gameObjects; // does NOT include the Player GameObject

	public Game() throws FileNotFoundException {
		Game.instance = this;
		GameWindow = new Display(this);
		GameWindow.setIcon(new Image(new ImageData("./res/icon.png")));
		gameObjects = new ArrayList<>();

		// create levels
		Levels = new HashMap<>();
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

		//setup player
		player = new Player();
		GameWindow.addImage(player.GetSprite());

		//load in first level
		currentLevel = Levels.get("title");
		currentLevel.Load();

		GameWindow.run();
	}

	public static void main(String[] args) throws FileNotFoundException{
		new Game();
	}

	public void Update() {
		if (GameWindow.GetKeyDown(GLFW_KEY_R)) {
			resetLevel();
			return;
		}

		player.Update();
		for (GameObject gameObject: gameObjects) {
			gameObject.Update();
		}
	}

	public void AddGameObject(GameObject gameObject) {
		gameObjects.add(gameObject);
		GameWindow.addImage(gameObject.GetSprite());
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

	public Player GetPlayer() {
		return player;
	}

	public Level GetCurrentLevel() {
		return currentLevel;
	}

	private void resetLevel() {
		player.Invert(false);
		player.position.replaceWith(currentLevel.startingPoint);
		// delete temp GameObjects
		// reset rest of GameObjects
	}

}