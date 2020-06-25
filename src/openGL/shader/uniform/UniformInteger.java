package openGL.shader.uniform;

import org.lwjgl.opengl.GL20;

public class UniformInteger extends Uniform{

	private int currentValue;

	/**
	 * Create a new Uniform of type int
	 * @param name name of the uniform, must be the same as in the Shader program
	 */
	public UniformInteger(String name){
		super(name);
	}
	
	public void loadInteger(int value){
		if(currentValue!=value){
			GL20.glUniform1i(super.getLocation(), value);
			currentValue = value;
		}
	}

}
