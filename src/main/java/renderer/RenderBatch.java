package renderer;

import components.SpriteRenderer;
import engine.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;

public class RenderBatch {

    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private int vaoID, vboID;
    private int batchSize;
    private Shader shader;

    public RenderBatch(int batchSize) {
        shader = new Shader("assets/shaders/default.glsl");
        shader.compileAndLink();
        this.sprites = new SpriteRenderer[batchSize];
        this.batchSize = batchSize;

        vertices = new float[batchSize * 4 * VERTEX_SIZE];
        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void start() {
        // create VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(2);
    }

    public void addSprite(SpriteRenderer sprite) {
        int index = this.numSprites;
        sprites[index] = sprite;
        this.numSprites++;

        loadVertexProperties(index);

        if (numSprites >= this.batchSize) {
            this.hasRoom = false;
        }
    }

    public void render() {
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        shader.use();
        shader.uploadMat4f("uProj", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    private int[] generateIndices() {

        int[] elements = new int[6*batchSize];
        for (int i=0; i < batchSize; i++) {
            loadElementIndices(elements, i);
        }
        return elements;

    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex+1] = offset + 2;
        elements[offsetArrayIndex+2] = offset;
        elements[offsetArrayIndex+3] = offset;
        elements[offsetArrayIndex+4] = offset + 2;
        elements[offsetArrayIndex+5] = offset + 1;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        float xAdd = 1.0f;
        float yAdd = 1.0f;

        for (int i=0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }


            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;
        }
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }
}
