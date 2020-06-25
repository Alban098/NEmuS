package openGL.shader.uniform;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {
	
	private static final int NOT_FOUND = -1;
	
	private String name;
	private int location;

	/**
	 * Create a new Uniform
	 * @param name name of the uniform, must be the same as in the Shader program
	 */
	Uniform(String name){
		this.name = name;
	}

	/**
	 * Allocate GPU RAM for this Uniform to the shader
	 * @param programID shader ID
	 */
	public void storeUniformLocation(int programID){
		location = GL20.glGetUniformLocation(programID, name);
		if(location == NOT_FOUND){
			System.err.println("Uniform \"" + name + "\" non trouvée pour le shader : "+programID);
		}
	}

	/**
	 * Return the location of the Uniform
	 * @return uniform location
	 */
	int getLocation(){
		return location;
	}

	public abstract void loadDefault();

	public String getName() {
		return name;
	}
}
