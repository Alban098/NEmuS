package openGL.shader.uniform;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class UniformMat4 extends Uniform{
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	private Matrix4f defaultValue;

	public UniformMat4(String name, Matrix4f defaultValue) {
		super(name);
		this.defaultValue = defaultValue;
	}

	@Override
	public Object getDefault() {
		return defaultValue;
	}

	public void loadDefault() {
		loadMatrix(defaultValue);
	}
	
	public void loadMatrix(Matrix4f matrix){
		matrix.get(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
	}
}
