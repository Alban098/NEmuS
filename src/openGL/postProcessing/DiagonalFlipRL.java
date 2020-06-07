package openGL.postProcessing;

import openGL.Quad;

public class DiagonalFlipRL extends PostProcessingStep{

    @Override
    public PostProcessingStep clone() {
        return new DiagonalFlipRL(quad, fbo.getWidth(), fbo.getHeight());
    }

    public DiagonalFlipRL(Quad quad) {
        super(quad, "shaders/d1_flip_vertex.glsl", "shaders/filters/no_filter.glsl");
    }

    public DiagonalFlipRL(Quad quad, int width, int height) {
        super(quad, "shaders/d1_flip_vertex.glsl", "shaders/filters/no_filter.glsl", width, height);
    }

    @Override
    public String toString() {
        return "Diagonal Flip RL";
    }
}
