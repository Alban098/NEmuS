package openGL.shader.uniform;

import org.joml.Matrix2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public class UniformMat2 extends Uniform{

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4);
	private Matrix2f defaultValue;

	public UniformMat2(String name, Matrix2f defaultValue) {
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

	public void loadMatrix(Matrix2f matrix){
		matrix.get(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix3fv(super.getLocation(), false, matrixBuffer);
	}
}
