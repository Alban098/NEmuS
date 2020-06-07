package openGL.postProcessing;

import core.ppu.PPU_2C02;
import gui.NEmuS_Release;
import openGL.Quad;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class PostProcessingPipeline {

    private Quad quad;

    private List<PostProcessingStep> allSteps;

    private List<PostProcessingStep> steps;

    //We need to duplicate the Filters when required, this need to be done by the OpenGL Thread
    //So we use a buffer variable to store the list of filters to apply
    private List<PostProcessingStep> requestedSteps;

    private PostProcessingStep default_filter;
    private boolean locked = false;

    public PostProcessingPipeline(Quad quad) {
        this.quad = quad;

        steps = new ArrayList<>();

        allSteps = new ArrayList<>();
        allSteps.add(new GaussianHorizontal(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new GaussianVertical(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new VerticalFlip(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new HorizontalFlip(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new DiagonalFlipLR(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new DiagonalFlipRL(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));

        default_filter = new VerticalFlip(quad);
    }

    public void applyFilters(int texture) {

        //If the pipeline has been modified, we lock the buffer and recompile the pipeline
        if (requestedSteps != null) {
            steps.clear();
            locked = true;
            for (PostProcessingStep step : requestedSteps) {
                step.cleanUp();
                steps.add(step.clone());
            }
            requestedSteps = null;
            locked = false;
        }

        //We apply each step of the pipeline
        start();
        if (steps.size() > 0) {
            steps.get(0).render(texture, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
            for (int i = 1; i < steps.size(); i++)
                steps.get(i).render(steps.get(i - 1).getOutputTexture(), PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
            default_filter.render(steps.get(steps.size() - 1).getOutputTexture(), NEmuS_Release.getInstance().getWidth(), NEmuS_Release.getInstance().getHeight());
        } else
            default_filter.render(texture, NEmuS_Release.getInstance().getWidth(), NEmuS_Release.getInstance().getHeight());
        end();
    }

    public void cleanUp(){
        default_filter.cleanUp();
    }

    private void start(){
        GL30.glBindVertexArray(quad.vaoId);
        GL20.glEnableVertexAttribArray(0);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private void end(){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public List<PostProcessingStep> getAllSteps() {
        return allSteps;
    }

    public void setSteps(List<PostProcessingStep> steps) {
        //We wait until the buffer is available before writing it from another Thread
        while (locked);
        requestedSteps = steps;
    }
}