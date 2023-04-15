package TTL;

import TTL.Sprite.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.*;
import java.util.*;
import javax.imageio.ImageIO;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Display {
	Game Game;

	private int windowWidth = 640;
	private int windowHeight = 640;
	private HashMap<Integer, ArrayList<Sprite>> sprites;
	private int numLayers = 0;

	// The window handle
	private long window;

	//Input Variables
	private DoubleBuffer xpos, ypos;
	private HashMap<Integer, Boolean> keysPrev;
	private HashMap<Integer, Boolean> keys;
	private int[] trackedKeys = {
			GLFW_KEY_D, // Duplicate Ability
			GLFW_KEY_F, // Flip Ability
			GLFW_KEY_L, // Level Select
			GLFW_KEY_R, // Restart
			GLFW_KEY_S, // Level Select
			GLFW_KEY_M, // Music Toggle
			GLFW_KEY_SPACE, // Jump
			GLFW_KEY_LEFT,  // Move Left
			GLFW_KEY_RIGHT  // Move Right
	};

	public Display(Game game) {
		Game = game;
		init();
		sprites = new HashMap<>();
		keysPrev = new HashMap<>();
		keys = new HashMap<>();
		xpos = BufferUtils.createDoubleBuffer(1);
		ypos = BufferUtils.createDoubleBuffer(1);
	}

	public void run() {
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		//glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(windowWidth, windowHeight, "Loading...", NULL, NULL);
		glfwSetWindowAspectRatio(window, 16, 9);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos( window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(2); // 30 fps

		// Make the window visible
		glfwShowWindow(window);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
	}

	private void loop() {
		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_TEXTURE_2D);

		// Enable transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Run the rendering loop until the user has attempted to close the window
		while (!glfwWindowShouldClose(window)) {
			setInputVariables();
			Game.Update();

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			renderSprites();
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

		Game.Cleanup();
	}

	private void setInputVariables() {
		for (int i : trackedKeys) {
			keysPrev.put(i, keys.put(i, glfwGetKey(window, i) == GLFW_TRUE));
		}
	}

	public boolean GetKeyHeld(int key) {
		return keys.get(key);
	}

	public boolean GetKeyDown(int key) {
		return keys.get(key) && !keysPrev.get(key);
	}

	public boolean GetKeyUp(int key) {
		return !keys.get(key) && keysPrev.get(key);
	}

	public boolean addSprite(Sprite sprite) {
		// abort if image is already marked as added to display
		if (sprite.AddedToDisplay || sprite.GetLayer() < 0) {
			return false;
		}

		// if layer doesn't exist, create it
		ArrayList<Sprite> list = sprites.get(sprite.GetLayer());
		if (list == null) {
		  list = new ArrayList<>();
			sprites.put(sprite.GetLayer(), list);
		  if (sprite.GetLayer() + 1 > numLayers) {
		    numLayers = sprite.GetLayer() + 1;
      }
    }

    // add image
    list.add(sprite);
		sprite.AddedToDisplay = true;
    return true;
	}

	public boolean removeSprite(Sprite sprite) {
	  if (!sprite.AddedToDisplay) {
	    return false;
    }
		sprites.get(sprite.GetLayer()).remove(sprite);
		sprite.AddedToDisplay = false;
    return true;
	}

	private void renderSprites() {
		for (int i = 0, len = numLayers; i < len; i++) {
		  ArrayList<Sprite> layer = sprites.get(i);
		  if (layer == null) continue;
			for (Sprite spr : layer) {
				spr.Render();
			}
		}
	}

	public void SetTitle(String title) {
		glfwSetWindowTitle(window, title);
	}

	public void SetIcon(String iconFilePath) {
		try {
			GLFWImage image = GLFWImage.malloc();
			BufferedImage bi = ImageIO.read(Game.ClassLoader.getResourceAsStream(iconFilePath));
			image.set(bi.getWidth(), bi.getHeight(), Image.createPixelBuffer(bi));
			GLFWImage.Buffer images = GLFWImage.malloc(1);
			images.put(0, image);
			glfwSetWindowIcon(window, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int GetWidth() {
		return windowWidth;
	}

	public int GetHeight() {
		return windowHeight;
	}
}
