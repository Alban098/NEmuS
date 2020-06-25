package openGL.shader.uniform;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

public class UniformVec2 extends Uniform {

	private float currentX;
	private float currentY;

	/**
	 * Create a new Uniform of type vec2
	 * @param name name of the uniform, must be the same as in the Shader program
	 */
	public UniformVec2(String name) {
		super(name);
	}

	/**
	 * Load a vector in GPU RAM
	 * @param vector vector to load
	 */
	public void loadVec2(Vector2f vector) {
		loadVec2(vector.x, vector.y);
	}

	/**
	 * Load a vector in GPU RAM
	 * @param x x component of the vector
	 * @param y y component of the vector
	 */
	public void loadVec2(float x, float y) {
		if (x != currentX || y != currentY) {
			this.currentX = x;
			this.currentY = y;
			GL20.glUniform2f(super.getLocation(), x, y);
		}
	}

}
