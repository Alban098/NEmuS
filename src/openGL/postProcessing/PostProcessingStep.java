package openGL.postProcessing;

import openGL.Fbo;
import openGL.Quad;
import openGL.ShaderProgram;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

/**
 * This class represents one filter that can be applied as a step of the post processing pipeline
 */
public abstract class PostProcessingStep {

    protected Quad quad;
    protected Fbo fbo;
    private ShaderProgram shader;

    /**
     * Create a new Filter from specific shaders
     * filters created with this will be rendered directly to the screen
     *
     * @param quad     the Quad where to render
     * @param vertex   the vertex shader file
     * @param fragment the fragment shader file
     */
    public PostProcessingStep(Quad quad, String vertex, String fragment) {
        this.quad = quad;
        try {
            shader = new ShaderProgram(vertex, fragment);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Create a new Filter from specific shaders that will be rendered in an FBO of a specific size
     *
     * @param quad     the Quad where to render
     * @param vertex   the vertex shader file
     * @param fragment the fragment shader file
     * @param width    the width of the FBO
     * @param height   the height of the FBO
     */
    public PostProcessingStep(Quad quad, String vertex, String fragment, int width, int height) {
        this.quad = quad;
        try {
            shader = new ShaderProgram(vertex, fragment);
        } catch (Exception e) {
            System.exit(-1);
        }
        fbo = new Fbo(width, height);
    }

    /**
     * Delete the shader and the FBO if it exists
     */
    public void cleanUp() {
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

        quad.render(width, height);

        glBindTexture(GL_TEXTURE_2D, 0);
        shader.unbind();

        if (fbo != null)
            fbo.unbindFrameBuffer();
    }

    /**
     * Return the FBO texture if it exist
     *
     * @return the FBO texture, 0 if no FBO exist
     */
    public int getOutputTexture() {
        if (fbo != null)
            return fbo.getTexture();
        return 0;
    }

    /**
     * Create a copy of the current filter with it's own shader and FBO
     *
     * @return a copy of the filter
     */
    public abstract PostProcessingStep clone();
}
