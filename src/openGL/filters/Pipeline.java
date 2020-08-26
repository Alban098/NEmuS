package openGL.filters;

import core.ppu.PPU_2C02;
import gui.lwjgui.NEmuSUnified;
import openGL.Fbo;
import openGL.Quad;
import openGL.shader.ShaderProgram;
import openGL.shader.uniform.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import utils.Dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glEnable;

/**
 * This class represents a post processing pipeline that can process an input texture
 */
public class Pipeline {

    private final Map<Filter, ShaderProgram> shaders;
    private final List<FilterInstance> appliedFilters;

    private boolean fboLatch = false;
    private final Quad quad;
    private final Fbo fbo1;
    private final Fbo fbo2;

    //We need to duplicate the Filters when required, this need to be done by the OpenGL Thread
    //So we use a buffer variable to store the list of filters to apply
    private List<FilterInstance> requestedSteps;
    private final Filter default_filter;

    private volatile boolean locked = false;

    /**
     * Create a new pipeline
     *
     * @param quad the quad where we will render the textures
     */
    public Pipeline(Quad quad) {
        this.quad = quad;
        this.fbo1 = new Fbo(PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
        this.fbo2 = new Fbo(PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT);
        appliedFilters = new ArrayList<>();
        default_filter = Filter.getDefault();
        shaders = new HashMap<>();
        try {
            for (Filter filter : Filter.getAll()) {
                shaders.put(filter, new ShaderProgram(filter.vertexFile, filter.fragmentFile).storeAllUniformLocations(filter.getAllUniforms()));
            }
            shaders.put(default_filter, new ShaderProgram(default_filter.vertexFile, default_filter.fragmentFile).storeAllUniformLocations(default_filter.getAllUniforms()));
        } catch (Exception e) {
            Dialogs.showException("Error compiling Shaders", "An error has occurred during Shader compilation", e);
        }
    }

    /**
     * Will apply the current set of filters to the input texture and render the result to the screen
     *
     * @param texture the texture we want to apply the filters to
     */
    public void postProcess(int texture) {
        //If the pipeline has been modified, we lock the buffer and recompile the pipeline
        while (locked) Thread.onSpinWait();
        locked = true;
        if (requestedSteps != null) {
            appliedFilters.clear();
            appliedFilters.addAll(requestedSteps);
            requestedSteps = null;
        }
        locked = false;

        //We apply each step of the pipeline
        start();
        if (appliedFilters.size() > 0) {
            int i = 0;
            for (FilterInstance filterInstance : appliedFilters) {
                if (i == 0) {
                    applyFilter(filterInstance.filter, filterInstance.parameters, texture, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT, false);
                } else {
                    applyFilter(filterInstance.filter, filterInstance.parameters, -1, PPU_2C02.SCREEN_WIDTH, PPU_2C02.SCREEN_HEIGHT, false);
                }
                i++;
            }
            applyFilter(default_filter, null, -1, NEmuSUnified.getInstance().getWidth(), NEmuSUnified.getInstance().getHeight(), true);
        } else {
            applyFilter(default_filter, null, texture, NEmuSUnified.getInstance().getWidth(), NEmuSUnified.getInstance().getHeight(), true);
        }
        end();
    }

    private void applyFilter(Filter filter, Parameter[] parameters, int texture, int width, int height, boolean toScreen) {
        if (shaders.get(filter) != null) {
            if (fboLatch) {
                if (!toScreen)
                    fbo1.bindFrameBuffer();
                glBindTexture(GL_TEXTURE_2D, fbo2.getTexture());
            } else {
                if (!toScreen)
                    fbo2.bindFrameBuffer();
                glBindTexture(GL_TEXTURE_2D, fbo1.getTexture());
            }
            if (!toScreen)
                fboLatch = !fboLatch;
            if (texture > 0)
                glBindTexture(GL_TEXTURE_2D, texture);

            shaders.get(filter).bind();

            //Preload default value in case parameters are missing
            for (Uniform u : filter.getAllUniforms())
                u.loadDefault();

            if (parameters != null) {
                for (Parameter param : parameters) {
                    Uniform u = filter.getUniform(param.name);
                    switch (param.type) {
                        case BOOLEAN:
                            if (u instanceof UniformBoolean)
                                ((UniformBoolean) u).loadBoolean((Boolean) param.value);
                            break;
                        case INTEGER:
                            if (u instanceof UniformInteger)
                                ((UniformInteger) u).loadInteger((Integer) param.value);
                            break;
                        case FLOAT:
                            if (u instanceof UniformFloat)
                                ((UniformFloat) u).loadFloat((Float) param.value);
                            break;
                        case VEC2:
                            if (u instanceof UniformVec2)
                                ((UniformVec2) u).loadVec2((Vector2f) param.value);
                            break;
                        case VEC3:
                            if (u instanceof UniformVec3)
                                ((UniformVec3) u).loadVec3((Vector3f) param.value);
                            break;
                        case VEC4:
                            if (u instanceof UniformVec4)
                                ((UniformVec4) u).loadVec4((Vector4f) param.value);
                            break;
                    }
                }
            }
            quad.render(width, height);
            glBindTexture(GL_TEXTURE_2D, 0);
            fbo1.unbindFrameBuffer();
            shaders.get(filter).unbind();
        }
    }

    /**
     * Clean up every filters of the pipeline
     */
    public void cleanUp() {
        for (ShaderProgram shader : shaders.values())
            shader.cleanUp();
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
     * Set the list of filters to be applied
     *
     * @param filters the list of filters to apply
     */
    public void setSteps(List<FilterInstance> filters) {
        //We wait until the buffer is available before writing it from another Thread
        while (locked) Thread.onSpinWait();
        requestedSteps = filters;
    }

    public List<FilterInstance> getSteps() {
        while (locked) Thread.onSpinWait();
        locked = true;
        List<FilterInstance> l = new ArrayList<>(appliedFilters);
        locked = false;
        return l;
    }
}