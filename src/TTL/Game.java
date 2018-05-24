package TTL;

import java.io.*;
import java.util.*;

import TTL.GameObject.*;
import TTL.Level.*;
import TTL.Sprite.*;
import fontMeshCreator.*;
import org.json.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
	public static Game instance;

	public Display GameWindow;
	public int[][] CurrentLevelMap;
	public boolean[][] ForcefieldMap;
	public HashMap<Integer, Boolean> SwitchIDs;

	private HashMap<String, Level> levels = new HashMap<>();
	private Level currentLevel;
	private String nextLevel;
	private HashMap<String, FontType> fonts;
	private Player player;
	private ArrayList<GameObject> gameObjects; // does NOT include the Player GameObject
	private Random random;
	private boolean musicPlaying;
	private int currentMusic;
	private String[] musicList = new String[0];

	private BackgroundImage bkgGray;
	private BackgroundImage bkgGear;
	private BackgroundImage bkgBlue;
	private BackgroundImage bkgGreen;
	private BackgroundImage bkgDigital;

	private MusicPlayer music;

	public Game() {
		Game.instance = this;
		GameWindow = new Display(this);
		GameWindow.SetIcon("./res/icon.png");
		gameObjects = new ArrayList<>();
		SwitchIDs = new HashMap<>();
		nextLevel = null;

		fonts = new HashMap<>();
		try {
			FileInputStream stream = new FileInputStream(new File("./res/fonts.json"));
			JSONObject root = new JSONObject(new JSONTokener(stream));
			JSONArray imageJSONArray = root.getJSONArray("Fonts");
			for (int i = 0; i < imageJSONArray.length(); i++) {
				JSONObject font = imageJSONArray.getJSONObject(i);
				fonts.put(font.getString("Name"), new FontType(new File(font.getString("PNG")), new File(font.getString("FNT"))));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Preload all images
		String[] imagesToPreload = new String[0];
		try {
			FileInputStream stream = new FileInputStream(new File("./res/images.json"));
			JSONObject root = new JSONObject(new JSONTokener(stream));
			JSONArray imageJSONArray = root.getJSONArray("Images");
			imagesToPreload = new String[imageJSONArray.length()];
			for (int i = 0; i < imageJSONArray.length(); i++) {
				imagesToPreload[i] = imageJSONArray.getString(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image.PreloadImages(imagesToPreload);

		importLevels();
//		levels.put("title", new LevelTitle());
//		levels.put("levelselect", new LevelSelect());
//		levels.put("1-1", new Level1x1());
//		levels.put("1-2", new Level1x2());
//		levels.put("1-3", new Level1x3());
//		levels.put("1-4", new Level1x4());
//		levels.put("1-5", new Level1x5());
//		levels.put("2-1", new Level2x1());
//		levels.put("2-2", new Level2x2());
//		levels.put("2-3", new Level2x3());
//		levels.put("2-4", new Level2x4());
//		levels.put("2-5", new Level2x5());
//		levels.put("3-1", new Level3x1());
//		levels.put("3-2", new Level3x2());
//		levels.put("3-3", new Level3x3());
//		levels.put("3-4", new Level3x4());
//		levels.put("3-5", new Level3x5());
//		levels.put("credits", new LevelCredits());

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
		player = new Player(false);
		GameWindow.addSprite(player.GetSprite());

		//load in first level
		loadLevel(levels.get("title"));

		random = new Random();
		musicPlaying = true;
		try { // load in music list
			FileInputStream stream = new FileInputStream(new File("./res/music.json"));
			JSONTokener tokener = new JSONTokener(stream);
			JSONObject root = new JSONObject(tokener);
			JSONArray musicJSONArray = root.getJSONArray("Music");
			musicList = new String[musicJSONArray.length()];
			for (int i = 0; i < musicJSONArray.length(); i++) {
				musicList[i] = musicJSONArray.getString(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		GameWindow.run();
	}

	public static void main(String[] args) throws FileNotFoundException{
		new Game();
	}

	public void Update() {
		if (musicList.length > 0 && (music == null || music.finished())) {
			currentMusic = random.nextInt(musicList.length);
			music = new MusicPlayer(musicList[currentMusic]);
			music.start();
		}

		// change level if one is slated to be loaded
		if (nextLevel != null) {
			Level level = levels.get(nextLevel);
			if (level != null) {
				for (GameObject gameObject : gameObjects) { // destroy all GameObjects
					GameWindow.removeSprite(gameObject.GetSprite());
				}
				gameObjects.clear();
				SwitchIDs.clear();
				loadLevel(level);
			}
			nextLevel = null;
		}

		// update player and GameObjects
		player.Update();
		for (GameObject gameObject: gameObjects) {
			gameObject.Update();
		}

		// if R is pressed, reset the level
		if (GameWindow.GetKeyDown(GLFW_KEY_R)) {
			player.Reset();
			SetBackgroundsInverted(currentLevel.GetStartInverted());
			for (GameObject gameObject : gameObjects) {
				gameObject.Reset();
			}
		}

		// if S is pressed, queue up LevelSelect
		if (GameWindow.GetKeyDown(GLFW_KEY_S)) {
			ChangeLevel("levelselect");
		}

		// if M is pressed, toggle the music
		if (music != null && GameWindow.GetKeyDown(GLFW_KEY_M)) {
			musicPlaying = !musicPlaying;
			music.pause(!musicPlaying);
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

	public void Cleanup() {
		if (music != null) {
			music.cleanup();
		}
	}

	public void ChangeLevel(String level) {
		nextLevel = level;
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

	public FontType GetFont(String fontName) {
		return fonts.get(fontName);
	}

	public int GetSpaceType(Vector2 coord) {
		// out of bounds
		if (coord.x < 0 || coord.x >= Game.instance.GameWindow.GetWidth() || coord.y < 0 || coord.y >= Game.instance.GameWindow.GetHeight()) {
			return Level.SPACE_INVALID;
		}

		return CurrentLevelMap[LocationToCoordinatesY(coord)][LocationToCoordinatesX(coord)];
	}

	public int LocationToCoordinatesX(Vector2 location) {
		return (int)(location.x / Game.instance.GameWindow.GetWidth() * CurrentLevelMap[0].length);
	}

	public int LocationToCoordinatesY(Vector2 location) {
		return (int)(location.y / Game.instance.GameWindow.GetHeight() * CurrentLevelMap.length);
	}

	public void SetBackgroundsInverted(boolean inverted) {
		bkgGreen.Visible = bkgDigital.Visible = inverted;
		bkgBlue.Visible = bkgGear.Visible = !inverted;
	}

	private void importLevels() {
		try {
			FileInputStream stream = new FileInputStream(new File("res/levels.json"));
			JSONObject root = new JSONObject(new JSONTokener(stream));
			JSONArray levelArray = root.getJSONArray("Levels");
			for (int i = 0; i < levelArray.length(); i++) {
				JSONObject levelObj = levelArray.getJSONObject(i);
				levels.put(levelObj.getString("Name"), new Level(levelObj));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadLevel(Level level) {
		currentLevel = level;
		CurrentLevelMap = new int[currentLevel.GetLevelMap().length][]; // deep clone
		for(int i = 0; i < currentLevel.GetLevelMap().length; i++) {
			CurrentLevelMap[i] = currentLevel.GetLevelMap()[i].clone();
		}
		ForcefieldMap = new boolean[CurrentLevelMap.length][CurrentLevelMap[0].length];
		player.Reset();
		currentLevel.Load();
	}
}
