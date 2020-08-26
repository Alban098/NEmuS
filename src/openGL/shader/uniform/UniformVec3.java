package openGL.shader.uniform;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class UniformVec3 extends Uniform {

	private float currentX;
	private float currentY;
	private float currentZ;
	private final Vector3f defaultValue;

	/**
	 * Create a new Uniform of type vec3
	 * @param name name of the uniform, must be the same as in the Shader program
	 */
	public UniformVec3(String name, Vector3f defaultValue) {
		super(name);
		this.defaultValue = defaultValue;
	}

	@Override
	public Object getDefault() {
		return defaultValue;
	}

	public void loadDefault() {
		loadVec3(defaultValue);
	}

	/**
	 * Load a vector in GPU RAM
	 * @param vector vector to load
	 */
	public void loadVec3(Vector3f vector) {
		loadVec3(vector.x, vector.y, vector.z);
	}

	/**
	 * Load a vector in GPU RAM
	 * @param x x component of the vector
	 * @param y y component of the vector
	 * @param z z component of the vector
	 */
	public void loadVec3(float x, float y, float z) {
		if (x != currentX || y != currentY || z != currentZ) {
			this.currentX = x;
			this.currentY = y;
			this.currentZ = z;
			GL20.glUniform3f(super.getLocation(), x, y, z);
		}
	}

}
