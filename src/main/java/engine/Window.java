package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import util.Time;

import java.nio.IntBuffer;
import java.util.Objects;

import static java.lang.Math.max;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public final int width;
    public final int height;
    private final String title;
    private long glfwWindow;
    public float r,g,b;
    public boolean changingScene;

    private static Window instance = null;

    private static Scene currentScene;

    private Window() {
        this.width = 1920 / 2;
        this.height = 1080 / 2;
        this.title = "engine";
        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.changingScene = false;
    }

    public static Window get() {
        if (instance == null) {
            instance = new Window();
        }
        return instance;
    }

    public static Scene getScene() {
        return get().currentScene;
    }

    public static void changeScene(int scene) {
        switch(scene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            case 2:
                currentScene = new PauseScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "Unknown scene '" + scene + "'";
                break;
        }
    }

    public void run() {
        System.out.println("Starting LWJGL " + Version.getVersion());

        // initialize window and start run loop
        init();
        loop();

        // terminate window and free error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void init() {
        // create error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // throw error if glfw init fails
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // set window hints, size, resizeable, etc.
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        // hook to mouse/key listener callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);


        // get thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            // get window size
            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            // get resolution
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // center window
            assert vidMode != null;
            glfwSetWindowPos(
                    glfwWindow,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        } // stack frame is popped automagically

        // make opengl context current
        glfwMakeContextCurrent(glfwWindow);
        // enable v-sync
        glfwSwapInterval(1);

        // make window visible
        glfwShowWindow(glfwWindow);

        // required for magic bindings
        GL.createCapabilities();

        // set starting scene
        Window.changeScene(0);

    }
    public void loop() {
        float beginTime = (float)glfwGetTime();
        float dt = -1.0f;
        float endTime;

        // run rendering loop until user closes window
        while (!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor(r, g, b, 1.0f);

            if (dt >= 0){
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            // get frame time dt
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;

//            System.out.println("FPS: " + (1.0f / dt));
        }
    }
}
