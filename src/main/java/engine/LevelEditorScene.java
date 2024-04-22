package engine;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import renderer.Shader;
import renderer.Texture;


public class LevelEditorScene extends Scene {

    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture boxTexture;

    private GameObject obj;

    public LevelEditorScene() {
    }


    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(), new Vector2f());

        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (float)(600 - xOffset * 2);
        float totalHeight = (float)(300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight /100.0f;

        for (int x=0; x < 100; x++) {
            for (int y=0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);

                GameObject go = new GameObject("Obj", new Transform(new Vector2f(xPos,yPos), new Vector2f(sizeX, sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                this.addGameObjectToScene(go);
            }
        }
    }

    @Override
    public void update(float dt) {
        System.out.println("FPS: " + 1.0/dt);

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
        this.renderer.render();

    }

}
