package openGL.postProcessing;

import openGL.Quad;
import openGL.shader.FishEyeShader;
import openGL.shader.ToonifyShader;

/**
 * This class represents a filter that flip the screen along the diagonal starting from the top left
 */
public class Toonify extends PostProcessingStep {

    public float edge_low = 0.2f;
    public float edge_high = 5.0f;

    /**
     * Create a new Filter from specific shaders that will be rendered in an FBO of a specific size
     *
     * @param quad   the Quad where to render
     * @param width  the width of the FBO
     * @param height the height of the FBO
     */
    Toonify(Quad quad, int width, int height) throws Exception {
        super(quad, new ToonifyShader(), width, height);
    }

    @Override
    void loadUniforms() {
        ((ToonifyShader)shader).edge_low.loadFloat(edge_low);
        ((ToonifyShader)shader).edge_high.loadFloat(edge_high);
    }

    /**
     * Create a copy of the current filter with it's own shader and FBO
     *
     * @return a copy of the filter
     */
    @Override
    PostProcessingStep cloneFilter() throws Exception {
        Toonify copy = new Toonify(quad, fbo.getWidth(), fbo.getHeight());
        copy.edge_low = edge_low;
        copy.edge_high = edge_high;
        return copy;
    }

    @Override
    public String toString() {
        return "Toonify";
    }
}
