package engine;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;

public class PauseScene extends Scene {

    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture boxTexture;

    public PauseScene() {
    }

    private float[] vertexArray = {
            //position                 //color                     //texture coords
            -50.0f,  50.0f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,     0, 1,    // Bottom right (0)
             50.0f, -50.0f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,     1, 0,    // Top left (1)
             50.0f,  50.0f, 0.0f,      0.0f, 0.0f, 1.0f, 1.0f,     1, 1,    // Top right (2)
            -50.0f, -50.0f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f,     0, 0,    // Bottom left (3)
    };

    private int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3 // Bottom left triangle
    };

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-Window.get().width/2.0f, -Window.get().height/2.0f), new Vector2f(-1,-1));
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

        // nostalgia screensaver
        camera.position.x += (dt * 100.0f) * camera.direction.x;
        camera.position.y +=  (dt * 80.0f) * camera.direction.y;

        if (Math.abs(camera.position.x) > 32.0f * 40.0f - 50 ){
            camera.direction.x *= -1;
        }
        if (Math.abs(camera.position.y) > 32.0f * 21.0f - 50) {
            camera.direction.y *= -1;
        }

        defaultShader.use();

        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        boxTexture.bind();

        defaultShader.uploadMat4f("uProj", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", (float)glfwGetTime());

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
    }

}
