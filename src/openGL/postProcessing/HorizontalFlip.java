package openGL.postProcessing;

import openGL.Quad;

public class HorizontalFlip extends PostProcessingStep{

    @Override
    public PostProcessingStep clone() {
        return new HorizontalFlip(quad, fbo.getWidth(), fbo.getHeight());
    }

    public HorizontalFlip(Quad quad) {
        super(quad, "shaders/h_flip_vertex.glsl", "shaders/filters/no_filter.glsl");
    }

    public HorizontalFlip(Quad quad, int width, int height) {
        super(quad, "shaders/h_flip_vertex.glsl", "shaders/filters/no_filter.glsl", width, height);
    }

    @Override
    public String toString() {
        return "Horizontal Flip";
    }
}
