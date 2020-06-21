package openGL.postProcessing;

import core.ppu.PPU_2C02;
import gui.interfaces.NEmuS_Runnable;
import gui.lwjgui.NEmuSUnified;
import openGL.Quad;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11C.glEnable;

/**
 * This class represents a post processing pipeline that can process an input texture
 */
public class PostProcessingPipeline {

    private List<PostProcessingStep> allSteps;
    private List<PostProcessingStep> steps;

    //We need to duplicate the Filters when required, this need to be done by the OpenGL Thread
    //So we use a buffer variable to store the list of filters to apply
    private List<PostProcessingStep> requestedSteps;

    private PostProcessingStep default_filter;

    private volatile boolean locked = false;

    /**
     * Create a new pipeline
     *
     * @param quad the quad where we will render the textures
     */
    public PostProcessingPipeline(Quad quad) {
        steps = new ArrayList<>();
        allSteps = new ArrayList<>();
        allSteps.add(new GaussianHorizontal(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new GaussianVertical(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new VerticalFlip(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new HorizontalFlip(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new DiagonalFlipTR(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));
        allSteps.add(new DiagonalFlipTL(quad, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT));

        //We need to apply a vertical flip because the PPU render from the top thus inverting the y component of the initial texture
        default_filter = new VerticalFlip(quad);
    }

    /**
     * Will apply the current set of filters to the input texture and render the result to the screen
     *
     * @param texture the texture we want to apply the filters to
     */
    public void applyFilters(int texture) {
        //If the pipeline has been modified, we lock the buffer and recompile the pipeline
        locked = true;
        if (requestedSteps != null) {
            for (PostProcessingStep step : steps)
                step.cleanUp();
            steps.clear();
            for (PostProcessingStep step : requestedSteps) {
                steps.add(step.cloneFilter());
            }
            requestedSteps = null;
        }
        locked = false;

        //We apply each step of the pipeline
        start();
        if (steps.size() > 0) {
            steps.get(0).render(texture, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
            for (int i = 1; i < steps.size(); i++)
                steps.get(i).render(steps.get(i - 1).getOutputTexture(), PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
            if (NEmuSUnified.getInstance() != null)
                default_filter.render(steps.get(steps.size() - 1).getOutputTexture(), NEmuSUnified.getInstance().getWidth() ,  NEmuSUnified.getInstance().getHeight());
            else
                default_filter.render(steps.get(steps.size() - 1).getOutputTexture(), 512, 480);
            //TODO remove when unified finished

        } else {
            if (NEmuSUnified.getInstance() != null)
                default_filter.render(texture, NEmuSUnified.getInstance().getWidth(), NEmuSUnified.getInstance().getHeight());
            else
                default_filter.render(texture, 512, 480);
        }
        end();
    }

    /**
     * Clean up every filters of the pipeline
     */
    public void cleanUp() {
        default_filter.cleanUp();
        for (PostProcessingStep step : steps)
            step.cleanUp();
    }

    /**
     * Prepare the pipeline for rendering
     */
    private void start() {
        glDisable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Reset the OpenGl state after rendering
     */
    private void end() {
        glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Get all existing filters
     *
     * @return a list of all existing filters
     */
    public List<PostProcessingStep> getAllSteps() {
        return allSteps;
    }

    /**
     * Set the list of filters to be applied
     *
     * @param steps the list of filters to apply
     */
    public void setSteps(List<PostProcessingStep> steps) {
        //We wait until the buffer is available before writing it from another Thread
        while (locked) Thread.onSpinWait();
        requestedSteps = steps;
    }
}