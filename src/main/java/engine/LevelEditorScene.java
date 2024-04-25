package engine;

import components.SpriteRenderer;
import org.joml.Vector2f;
import util.AssetPool;


public class LevelEditorScene extends Scene {

    public LevelEditorScene() {
    }


    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(), new Vector2f());

        GameObject mario = new GameObject("Mario", new Transform(new Vector2f(100,100), new Vector2f(256,256)));
        mario.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/textures/mario.png")));
        this.addGameObjectToScene(mario);

        GameObject goomba = new GameObject("Goomba", new Transform(new Vector2f(400,100), new Vector2f(256,256)));
        goomba.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/textures/goomba.png")));
        this.addGameObjectToScene(goomba);

        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
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
