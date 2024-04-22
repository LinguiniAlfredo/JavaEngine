package renderer;

import components.SpriteRenderer;
import engine.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private final int BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject obj) {
        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
        if (sprite != null) {
            add(sprite);
        }
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom()) {
                batch.addSprite(sprite);
                added = true;
                break;
            }
        }
        if (!added) {
            RenderBatch newBatch = new RenderBatch(BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
