package openGL.postProcessing;

import openGL.Quad;

public class VerticalFlip extends PostProcessingStep{

    @Override
    public PostProcessingStep clone() {
        return new VerticalFlip(quad, fbo.getWidth(), fbo.getHeight());
    }

    public VerticalFlip(Quad quad) {
        super(quad, "shaders/v_flip_vertex.glsl", "shaders/filters/no_filter.glsl");
    }

    public VerticalFlip(Quad quad, int width, int height) {
        super(quad, "shaders/v_flip_vertex.glsl", "shaders/filters/no_filter.glsl", width, height);
    }

    @Override
    public String toString() {
        return "Vertical Flip";
    }
}
