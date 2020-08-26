package openGL.shader.uniform;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;


public class UniformVec4 extends Uniform {

	private float currentX;
	private float currentY;
	private float currentZ;
	private float currentW;
	private final Vector4f defaultValue;

	/**
	 * Create a new Uniform of type vec4
	 * @param name name of the uniform, must be the same as in the Shader program
	 */
	public UniformVec4(String name, Vector4f defaultValue) {
		super(name);
		this.defaultValue = defaultValue;
	}

	@Override
	public Object getDefault() {
		return defaultValue;
	}

	public void loadDefault() {
		loadVec4(defaultValue);
	}

	/**
	 * Load a vector in GPU RAM
	 * @param vector vector to load
	 */
	public void loadVec4(Vector4f vector) {
		loadVec4(vector.x, vector.y, vector.z, vector.w);
	}

	/**
	 * Load a vector in GPU RAM
	 * @param x x component of the vector
	 * @param y y component of the vector
	 * @param z z component of the vector
	 * @param w x component of the vector
	 */
	public void loadVec4(float x, float y, float z, float w) {
		if (x != currentX || y != currentY || z != currentZ || w != currentW) {
			this.currentX = x;
			this.currentY = y;
			this.currentZ = z;
			this.currentW = w;
			GL20.glUniform4f(super.getLocation(), x, y, z, w);
		}
	}

}
