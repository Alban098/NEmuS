package openGL.postProcessing;

import openGL.Fbo;
import openGL.Quad;
import openGL.ShaderProgram;

import static org.lwjgl.opengl.GL11.*;

public abstract class PostProcessingStep {

    protected Quad quad;
    protected ShaderProgram shader;
    protected Fbo fbo;

    public PostProcessingStep(Quad quad, String vertex, String fragment) {
        this.quad = quad;
        try {
            shader = new ShaderProgram(vertex, fragment);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public PostProcessingStep(Quad quad, String vertex, String fragment, int width, int height) {
        this.quad = quad;
        try {
            shader = new ShaderProgram(vertex, fragment);
        } catch (Exception e) {
            System.exit(-1);
        }
        fbo = new Fbo(width, height);
    }

    public void cleanUp() {
        if (fbo != null)
            fbo.cleanUp();
        shader.cleanUp();
    }

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

    public int getOutputTexture() {
        if (fbo != null)
            return fbo.getTexture();
        return 0;
    }

    public abstract PostProcessingStep clone();
}
