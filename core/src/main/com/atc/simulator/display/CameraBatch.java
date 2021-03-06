package com.atc.simulator.display;

import com.atc.simulator.display.view.DisplayRenderableProvider;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;

import java.util.Collection;
import java.util.HashMap;

/**
 * Represents a collection of renderable providers to be rendered with the same camera.
 * @author Luke Frisken
 */
public class CameraBatch extends HashMap<DisplayRenderableProvider, RenderableProvider>
{
    private Camera camera;
    private Shader shader=null;

    /**
     * Constructor for CameraBatch
     * @param camera the camera that will be rendering the objects in the batch.
     */
    public CameraBatch(Camera camera)
    {
        super();
        this.camera = camera;
    }

    /**
     * Constructor for CameraBatch
     * @param camera the camera that will be rendering the objects in the batch.
     */
    public CameraBatch(Camera camera, Shader shader)
    {
        this(camera);
        this.shader = shader;
    }

    /**
     * Whether or not this camera batch has a shader
     * @return if the batch has a shader.
     */
    public boolean hasShader()
    {
        return shader != null;
    }

    /**
     * Get the shader to be used on this camera batch
     * @return Shader is returned for use with the camera batch
     */
    public Shader getShader()
    {
        return shader;
    }


    /**
     * Get the camera that will be rendering the objects in the batch
     * @return gets the Camera
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * Get the values contained in this batch.
     * @return gets values contained in batch, Collection of renderableProviders
     */
    public Collection<RenderableProvider> gdxRenderableProviders()
    {
        return values();
    }
}
