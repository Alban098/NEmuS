package openGL.postProcessing;

import openGL.Quad;

/**
 * This class represents a filter that will apply a vertical Gaussian Blur
 */
class GaussianVertical extends PostProcessingStep {

    /**
     * Create a new Filter from specific shaders that will be rendered in an FBO of a specific size
     *
     * @param quad   the Quad where to render
     * @param width  the width of the FBO
     * @param height the height of the FBO
     */
    GaussianVertical(Quad quad, int width, int height) {
        super(quad, "shaders/vertex.glsl", "shaders/filters/gaussian_vertical.glsl", width, height);
    }

    /**
     * Create a copy of the current filter with it's own shader and FBO
     *
     * @return a copy of the filter
     */
    @Override
    PostProcessingStep cloneFilter() {
        return new GaussianVertical(quad, fbo.getWidth(), fbo.getHeight());
    }

    @Override
    public String toString() {
        return "Gaussian Vertical";
    }
}
