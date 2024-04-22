package components;

import engine.Component;

public class FontRenderer extends Component {

    private boolean firstTime = false;

    @Override
    public void start() {
        System.out.println("FontRenderer starting");

    }

    @Override
    public void update(float dt) {
        if (!firstTime) {
            System.out.println("FontRenderer updating");
            firstTime = true;
        }
    }
}
