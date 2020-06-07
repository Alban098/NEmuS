package openGL.postProcessing;

import openGL.Quad;

public class GaussianVertical extends PostProcessingStep {

    @Override
    public PostProcessingStep clone() {
        return new GaussianVertical(quad, fbo.getWidth(), fbo.getHeight());
    }

    public GaussianVertical(Quad quad) {
        super(quad, "shaders/vertex.glsl", "shaders/filters/gaussian_vertical.glsl");
    }

    public GaussianVertical(Quad quad, int width, int height) {
        super(quad, "shaders/vertex.glsl", "shaders/filters/gaussian_vertical.glsl", width, height);
    }

    @Override
    public String toString() {
        return "Gaussian Vertical";
    }
}
