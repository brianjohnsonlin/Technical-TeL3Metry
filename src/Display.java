import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Display {
	Game Game;

	private int windowWidth = 640;
	private int windowHeight = 640;
	private HashMap<Integer, ArrayList<Image>> images;
	private int numLayers = 0;

	// The window handle
	private long window;

	//Input Variables
	private DoubleBuffer xpos, ypos;
	private HashMap<Integer, Boolean> mousePrev;
	private HashMap<Integer, Boolean> mouse;
	private HashMap<Integer, Boolean> keysPrev;
	private HashMap<Integer, Boolean> keys;
	private int[] trackedKeys = {
			GLFW_KEY_D, // Duplicate Ability
			GLFW_KEY_F, // Flip Ability
			GLFW_KEY_L, // Level Select
			GLFW_KEY_R, // Restart
			GLFW_KEY_M, // Music Toggle
			GLFW_KEY_SPACE, // Jump
			GLFW_KEY_LEFT,  // Move Left
			GLFW_KEY_RIGHT  // Move Right
	};
	private int[] trackedMouse = {
			// GLFW_MOUSE_BUTTON_LEFT,
			// GLFW_MOUSE_BUTTON_RIGHT
	};

	public Display(Game game) {
		Game = game;
		init();
		images = new HashMap<>();
		keysPrev = new HashMap<>();
		keys = new HashMap<>();
		mousePrev = new HashMap<>();
		mouse = new HashMap<>();
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
		window = glfwCreateWindow(windowWidth, windowHeight, "Technial TeL3Metry", NULL, NULL);
		glfwSetWindowAspectRatio(window, 16, 9);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

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
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);

		GL.createCapabilities();
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_TEXTURE_2D);

		// Enable transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {
			setInputVariables();
			Game.Update();

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			renderImages();
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	private void setInputVariables() {
		glfwGetCursorPos(window, xpos, ypos);
		for (int i : trackedMouse) {
			mousePrev.put(i, mouse.put(i, glfwGetMouseButton(window, i) == GLFW_TRUE));
		}
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

	public boolean GetMouseHeld(int mouseButton) {
		return mouse.get(mouseButton);
	}

	public boolean GetMouseDown(int mouseButton) {
		return mouse.get(mouseButton) && !mousePrev.get(mouseButton);
	}

	public boolean GetMouseUp(int mouseButton) {
		return !mouse.get(mouseButton) && mousePrev.get(mouseButton);
	}

	public Vector2 GetMousePosition() {
		return new Vector2((float)xpos.get(0), (float)ypos.get(0));
	}

//    public boolean GetMouseOver(Image image) {
//        return GetMouseX() >= image.position.x && GetMouseY() >= image.position.y &&
//               GetMouseX() <= image.position.x + image.width && GetMouseY() <= image.position.y + image.height;
//    }
//
//    public boolean GetMouseOver(double x, double y, double radius) {
//        return radius >= Math.sqrt(Math.pow(GetMouseX() - x, 2) + Math.pow(GetMouseY() - y, 2));
//    }

	public boolean addImage(Image image) {
		// abort if image is null or it's already marked as added to display
	    if (image == null || image.addedToDisplay || image.GetLayer() < 0) {
			return false;
		}

		// if layer doesn't exist, create it
		ArrayList<Image> list = images.get(image.GetLayer());
		if (list == null) {
		    list = new ArrayList<>();
		    images.put(image.GetLayer(), list);
		    if (image.GetLayer() + 1 > numLayers) {
		        numLayers = image.GetLayer() + 1;
            }
        }

        // add image
        list.add(image);
        image.addedToDisplay = true;
        return true;
	}

	public boolean removeImage(Image image) {
	    if (!image.addedToDisplay) {
	        return false;
        }
		for (ArrayList<Image> list : images.values()) {
	        for (int i = 0, len = list.size(); i < len; i++) {
	            if (list.get(i) == image) list.remove(i);
	            return true;
            }
        }
        return false;
	}

	// returns number of images removed
	public int clearLayer(int layer) {
		ArrayList<Image> removedLayer = images.remove(layer);
		return removedLayer == null ? 0 : removedLayer.size();
	}

	private void renderImages() {
		for (int i = 0, len = numLayers; i < len; i++) {
		    ArrayList<Image> layer = images.get(i);
		    if (layer == null) continue;
			for (Image img : layer) {
			    if (!img.visible) continue;
				img.Bind();
				glBegin(GL_QUADS);
				{
					glTexCoord2f(img.GetLeftSpriteCoord(), img.GetTopSpriteCoord());
					glVertex2f((img.position.x / windowWidth * 2) - 1, -(img.position.y / windowHeight * 2) + 1);

					glTexCoord2f(img.GetLeftSpriteCoord(), img.GetBottomSpriteCoord());
					glVertex2f((img.position.x / windowWidth * 2) - 1, -((img.position.y + img.height) / windowHeight * 2) + 1);

					glTexCoord2f(img.GetRightSpriteCoord(), img.GetBottomSpriteCoord());
					glVertex2f(((img.position.x + img.width) / windowWidth * 2) - 1, -((img.position.y + img.height)/windowHeight*2) + 1);

					glTexCoord2f(img.GetRightSpriteCoord(), img.GetTopSpriteCoord());
					glVertex2f(((img.position.x + img.width) / windowWidth * 2) - 1, -(img.position.y / windowHeight * 2) + 1);
				}
				glEnd();
			}
		}
	}

	public void setIcon(Image icon) {
		GLFWImage image = GLFWImage.malloc();
		image.set((int)icon.width, (int)icon.height, icon.GetPixelBuffer());
		GLFWImage.Buffer images = GLFWImage.malloc(1);
		images.put(0, image);
		glfwSetWindowIcon(window, images);
	}
}