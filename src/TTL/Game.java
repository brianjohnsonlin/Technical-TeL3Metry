package TTL;

import java.io.*;
import java.util.*;

import TTL.GameObject.*;
import TTL.Level.*;
import fontMeshCreator.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
	public static Game instance;

	public Display GameWindow;
	public HashMap<String, Level> Levels;

	private Level currentLevel;
	private Player player;
	private ArrayList<GameObject> gameObjects; // does NOT include the Player GameObject
	private String nextLevel;
	private HashMap<String, FontType> fonts;

	private BackgroundImage bkgGray;
	private BackgroundImage bkgGear;
	private BackgroundImage bkgBlue;
	private BackgroundImage bkgGreen;
	private BackgroundImage bkgDigital;

	public Game() {
		Game.instance = this;
		GameWindow = new Display(this);
		GameWindow.SetIcon("./res/icon.png");
		gameObjects = new ArrayList<>();
		nextLevel = null;

		fonts = new HashMap<>();
		fonts.put("Opificio", new FontType(new File("./res/Opificio.png"), new File("./res/Opificio.fnt")));

		// create levels
		Levels = new HashMap<>();
		Levels.put("title", new LevelTitle());
		Levels.put("levelselect", new LevelSelect());
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

		//backgrounds
		bkgGray = new BackgroundImage("./res/bkg_gray.png", Level.SPACE_GRAY);
		bkgGear = new BackgroundImage("./res/bkg_gear.png", Level.SPACE_WHITE);
		bkgBlue = new BackgroundImage("./res/bkg_blue.png", Level.SPACE_BLACK);
		bkgGreen = new BackgroundImage("./res/bkg_green.png", Level.SPACE_WHITE);
		bkgDigital = new BackgroundImage("./res/bkg_digital.png", Level.SPACE_BLACK);
		Game.instance.GameWindow.addSprite(bkgGray);
		Game.instance.GameWindow.addSprite(bkgGear);
		Game.instance.GameWindow.addSprite(bkgBlue);
		Game.instance.GameWindow.addSprite(bkgGreen);
		Game.instance.GameWindow.addSprite(bkgDigital);

		//setup player
		player = new Player();
		GameWindow.addSprite(player.GetSprite());

		//load in first level
		currentLevel = Levels.get("title");
		currentLevel.Load();

		GameWindow.run();
	}

	public static void main(String[] args) throws FileNotFoundException{
		new Game();
	}

	public void Update() {
		// change level if one is slated to be loaded
		if (nextLevel != null) {
			loadLevel(nextLevel);
			nextLevel = null;
		}

		// if R is pressed, reset the level
		if (GameWindow.GetKeyDown(GLFW_KEY_R)) {
			resetLevel();
			return;
		}

		// update player and GameObjects
		player.Update();
		for (GameObject gameObject: gameObjects) {
			gameObject.Update();
		}

		// delete anything slated for removal
		for (int i = 0; i < gameObjects.size(); i++) {
			GameObject gobj = gameObjects.get(i);
			if (gobj.SlatedForDestruction) {
				gameObjects.remove(i);
				if (gobj.GetSprite() != null) {
					GameWindow.removeSprite(gobj.GetSprite());
				}
				i--;
			}
		}
	}

	public void AddGameObject(GameObject gameObject) {
		gameObjects.add(gameObject);
		if (gameObject.GetSprite() != null) {
			GameWindow.addSprite(gameObject.GetSprite());
		}
	}

	public Player GetPlayer() {
		return player;
	}

	public Level GetCurrentLevel() {
		return currentLevel;
	}

	public void ChangeLevel(String level) {
		nextLevel = level;
	}

	private void resetLevel() {
		player.Reset();
		for (GameObject gameObject : gameObjects) {
			gameObject.Reset();
		}
	}

	private void loadLevel(String levelname) {
		Level level = Levels.get(levelname);
		if (level != null) {
			currentLevel.Unload();
			currentLevel = level;
			currentLevel.Load();
		}
	}

	public FontType GetFont(String fontName) {
		return fonts.get(fontName);
	}

	public void DestroyAllGameObjects() {
		for (GameObject gameObject : gameObjects) {
			GameWindow.removeSprite(gameObject.GetSprite());
		}
		gameObjects.clear();
	}

	public void SetBackgroundsInverted(boolean inverted) {
		bkgGreen.Visible = bkgDigital.Visible = inverted;
		bkgBlue.Visible = bkgGear.Visible = !inverted;
	}

}
