package openGL.postProcessing;

import openGL.Quad;
import openGL.shader.GaussianHorizontalShader;

/**
 * This class represents a filter that will apply a horizontal Gaussian Blur
 */
class GaussianHorizontal extends PostProcessingStep {

    /**
     * Create a new Filter from specific shaders that will be rendered in an FBO of a specific size
     *
     * @param quad   the Quad where to render
     * @param width  the width of the FBO
     * @param height the height of the FBO
     */
    GaussianHorizontal(Quad quad, int width, int height) throws Exception {
        super(quad, new GaussianHorizontalShader(), width, height);
    }

    /**
     * Create a copy of the current filter with it's own shader and FBO
     *
     * @return a copy of the filter
     */
    @Override
    PostProcessingStep cloneFilter() throws Exception {
        return new GaussianHorizontal(quad, fbo.getWidth(), fbo.getHeight());
    }

    @Override
    public String toString() {
        return "Gaussian Horizontal";
    }
}
