package openGL.postProcessing;

import openGL.Quad;

public class DiagonalFlipLR extends PostProcessingStep{

    @Override
    public PostProcessingStep clone() {
        return new DiagonalFlipLR(quad, fbo.getWidth(), fbo.getHeight());
    }

    public DiagonalFlipLR(Quad quad) {
        super(quad, "shaders/d2_flip_vertex.glsl", "shaders/filters/no_filter.glsl");
    }

    public DiagonalFlipLR(Quad quad, int width, int height) {
        super(quad, "shaders/d2_flip_vertex.glsl", "shaders/filters/no_filter.glsl", width, height);
    }

    @Override
    public String toString() {
        return "Diagonal Flip LR";
    }
}
