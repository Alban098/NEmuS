package openGL.shader.uniform;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform{
	
	private float currentValue;
	private float defaultValue;

	/**
	 * Create a new Uniform of type float
	 * @param name name of the uniform, must be the same as in the Shader program
	 */
	public UniformFloat(String name, float defaultValue){
		super(name);
		this.defaultValue = defaultValue;
	}

	@Override
	public Object getDefault() {
		return defaultValue;
	}

	public void loadDefault() {
		loadFloat(defaultValue);
	}
	
	public void loadFloat(float value){
		if(currentValue!=value){
			GL20.glUniform1f(super.getLocation(), value);
			currentValue = value;
		}
	}

}
