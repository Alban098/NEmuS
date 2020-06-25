package openGL.postProcessing;

import openGL.Fbo;
import openGL.Quad;
import openGL.shader.ShaderProgram;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

/**
 * This class represents one filter that can be applied as a step of the post processing pipeline
 */
public abstract class PostProcessingStep {

    final Quad quad;
    ShaderProgram shader;
    Fbo fbo;

    /**
     * Create a new Filter from specific shaders
     * filters created with this will be rendered directly to the screen
     * @param quad     the Quad where to render
     *
     */
    PostProcessingStep(Quad quad, ShaderProgram shader) {
        this.quad = quad;
        this.shader = shader;
    }

    /**
     * Create a new Filter from specific shaders that will be rendered in an FBO of a specific size
     *
     * @param quad     the Quad where to render
     * @param shader   the shader program
     * @param width    the width of the FBO
     * @param height   the height of the FBO
     */
    PostProcessingStep(Quad quad, ShaderProgram shader, int width, int height) {
        this.quad = quad;
        this.shader = shader;
        fbo = new Fbo(width, height);
    }

    /**
     * Delete the shader and the FBO if it exists
     */
    void cleanUp() {
        if (fbo != null)
            fbo.cleanUp();
        shader.cleanUp();
    }

    /**
     * Apply the filter to the passed texture
     *
     * @param textureId the texture to which the filter will be applied
     * @param width     the rendering target width
     * @param height    the rendering target height
     */
    public void render(int textureId, int width, int height) {
        if (fbo != null)
            fbo.bindFrameBuffer();
        shader.bind();
        glBindTexture(GL_TEXTURE_2D, textureId);
        loadUniforms();
        quad.render(width, height);

        glBindTexture(GL_TEXTURE_2D, 0);
        shader.unbind();

        if (fbo != null)
            fbo.unbindFrameBuffer();
    }

    void loadUniforms() { }

    /**
     * Return the FBO texture if it exist
     *
     * @return the FBO texture, 0 if no FBO exist
     */
    int getOutputTexture() {
        if (fbo != null)
            return fbo.getTexture();
        return 0;
    }

    /**
     * Create a copy of the current filter with it's own shader and FBO
     *
     * @return a copy of the filter
     */
    abstract PostProcessingStep cloneFilter() throws Exception;
}
