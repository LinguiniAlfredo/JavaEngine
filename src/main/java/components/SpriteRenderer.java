package components;

import engine.Component;

public class SpriteRenderer extends Component {

    private boolean firstTime = false;

    @Override
    public void start() {
        System.out.println("SpriteRenderer starting");
    }

    @Override
    public void update(float dt) {
        if (!firstTime) {
            System.out.println("SpriteRenderer updating");
            firstTime = true;
        }
    }
}
