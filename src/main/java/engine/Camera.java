package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix;
    public Vector2f position;
    public Vector2f direction;

    public Camera(Vector2f position, Vector2f direction) {
        this.position = position;
        this.direction = direction;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
    }

    public Matrix4f getViewMatrix() {

        // TODO - If weird things happen come back here ***************

        Vector3f cameraFront = new Vector3f(position.x, position.y, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f eye = new Vector3f(position.x, position.y, 20.0f);

        this.viewMatrix.identity();
        this.viewMatrix.lookAt(eye, cameraFront, cameraUp);
        return this.viewMatrix;
    }
    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }
}
