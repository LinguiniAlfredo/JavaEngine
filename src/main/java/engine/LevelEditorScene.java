package engine;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static java.lang.Math.max;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;

public class LevelEditorScene extends Scene {

    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture boxTexture;

    private GameObject obj;

    private boolean firstTime = true;

    public LevelEditorScene() {
    }

    private float[] vertexArray = {
            //position                 //color                     //texture coords
            100.0f,  -0.0f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,     1, 1,    // Bottom right (0)
             -0.0f, 100.0f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,     0, 0,   // Top left (1)
            100.0f, 100.0f, 0.0f,      0.0f, 0.0f, 1.0f, 1.0f,     1, 0,    // Top right (2)
             -0.0f,   0.0f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f,     0, 1,    // Bottom left (3)
    };

    private int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3 // Bottom left triangle
    };

    @Override
    public void init() {
        System.out.println("Creating test object");
        this.obj = new GameObject("test object");
        this.obj.addComponent(new SpriteRenderer());
        this.addGameObjectToScene(this.obj);


        this.camera = new Camera(new Vector2f(), new Vector2f(-1,-1));
        this.defaultShader = new Shader("assets/shaders/default.glsl");
        this.boxTexture = new Texture("assets/textures/box.png");

        defaultShader.compileAndLink();

        // create VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // create VBO with float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // create EBO with int buffer of elements
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // define vertex attributes
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeInBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, positionsSize * Float.BYTES);
        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeInBytes, (positionsSize + colorSize) * Float.BYTES);
    }

    @Override
    public void update(float dt) {

        defaultShader.use();

        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        boxTexture.bind();

        defaultShader.uploadMat4f("uProj", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        glBindVertexArray(0);
        defaultShader.detach();

        if (firstTime) {
            System.out.println("Creating game object 2");
            GameObject go = new GameObject("Sprite Renderer 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = false;
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }

}
