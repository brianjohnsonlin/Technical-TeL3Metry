package TTL;

import java.io.*;
import java.util.*;

import TTL.GameObject.*;
import TTL.Sprite.*;
import fontMeshCreator.*;
import org.json.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
	public static Game instance;
	public static ClassLoader ClassLoader;

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
		ClassLoader = getClass().getClassLoader();
		GameWindow = new Display(this);
		GameWindow.SetIcon("icon.png");
		gameObjects = new ArrayList<>();
		SwitchIDs = new HashMap<>();
		nextLevel = null;

		// load in fonts
		fonts = new HashMap<>();
		{
			InputStream stream = ClassLoader.getResourceAsStream("fonts.json");
			JSONObject root = new JSONObject(new JSONTokener(stream));
			JSONArray imageJSONArray = root.getJSONArray("Fonts");
			for (int i = 0; i < imageJSONArray.length(); i++) {
				JSONObject font = imageJSONArray.getJSONObject(i);
				fonts.put(font.getString("Name"), new FontType(ClassLoader.getResourceAsStream(font.getString("PNG")), ClassLoader.getResourceAsStream(font.getString("FNT"))));
			}
		}

		// Preload all images
		{
			InputStream stream = ClassLoader.getResourceAsStream("images.json");
			JSONObject root = new JSONObject(new JSONTokener(stream));
			JSONArray imageJSONArray = root.getJSONArray("Images");
			String[] imagesToPreload = new String[imageJSONArray.length()];
			for (int i = 0; i < imageJSONArray.length(); i++) {
				imagesToPreload[i] = imageJSONArray.getString(i);
			}
			Image.PreloadImages(imagesToPreload);
		}

		//backgrounds
		bkgGray = new BackgroundImage("bkg_gray.png", Level.SPACE_GRAY);
		bkgGear = new BackgroundImage("bkg_gear.png", Level.SPACE_WHITE);
		bkgBlue = new BackgroundImage("bkg_blue.png", Level.SPACE_BLACK);
		bkgGreen = new BackgroundImage("bkg_green.png", Level.SPACE_WHITE);
		bkgDigital = new BackgroundImage("bkg_digital.png", Level.SPACE_BLACK);
		Game.instance.GameWindow.addSprite(bkgGray);
		Game.instance.GameWindow.addSprite(bkgGear);
		Game.instance.GameWindow.addSprite(bkgBlue);
		Game.instance.GameWindow.addSprite(bkgGreen);
		Game.instance.GameWindow.addSprite(bkgDigital);

		//setup player
		player = new Player(false);
		GameWindow.addSprite(player.GetSprite());

		// load in levels
		importLevels();
		loadLevel(levels.get("title"));

		// load in music list
		random = new Random();
		musicPlaying = true;
		{
			InputStream stream = ClassLoader.getResourceAsStream("music.json");
			JSONTokener tokener = new JSONTokener(stream);
			JSONObject root = new JSONObject(tokener);
			JSONArray musicJSONArray = root.getJSONArray("Music");
			musicList = new String[musicJSONArray.length()];
			for (int i = 0; i < musicJSONArray.length(); i++) {
				musicList[i] = musicJSONArray.getString(i);
			}
		}

		GameWindow.run();
	}

	public static void main(String[] args) {
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
		InputStream stream = ClassLoader.getResourceAsStream("levels.json");
		JSONObject root = new JSONObject(new JSONTokener(stream));
		JSONArray levelArray = root.getJSONArray("Levels");
		for (int i = 0; i < levelArray.length(); i++) {
			InputStream levelStream = ClassLoader.getResourceAsStream(levelArray.getString(i));
			JSONObject levelObj = new JSONObject(new JSONTokener(levelStream));
			levels.put(levelObj.getString("Name"), new Level(levelObj));
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
