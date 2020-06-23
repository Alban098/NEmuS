package openGL.postProcessing;

import openGL.Quad;

/**
 * This class represents a filter that flip the screen along the diagonal starting from the top right
 */
class DiagonalFlipTR extends PostProcessingStep {

    /**
     * Create a new Filter from specific shaders that will be rendered in an FBO of a specific size
     *
     * @param quad   the Quad where to render
     * @param width  the width of the FBO
     * @param height the height of the FBO
     */
    DiagonalFlipTR(Quad quad, int width, int height) {
        super(quad, "shaders/d2_flip_vertex.glsl", "shaders/filters/no_filter.glsl", width, height);
    }

    /**
     * Create a copy of the current filter with it's own shader and FBO
     *
     * @return a copy of the filter
     */
    @Override
    PostProcessingStep cloneFilter() {
        return new DiagonalFlipTR(quad, fbo.getWidth(), fbo.getHeight());
    }

    @Override
    public String toString() {
        return "Diagonal Flip Top Right";
    }
}
