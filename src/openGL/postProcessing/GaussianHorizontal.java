package openGL.postProcessing;

import openGL.Quad;

public class GaussianHorizontal extends PostProcessingStep {

    @Override
    public PostProcessingStep clone() {
        return new GaussianHorizontal(quad, fbo.getWidth(), fbo.getHeight());
    }

    public GaussianHorizontal(Quad quad) {
        super(quad, "shaders/vertex.glsl", "shaders/filters/gaussian_horizontal.glsl");
    }

    public GaussianHorizontal(Quad quad, int width, int height) {
        super(quad, "shaders/vertex.glsl", "shaders/filters/gaussian_horizontal.glsl", width, height);
    }

    @Override
    public String toString() {
        return "Gaussian Horizontal";
    }
}
