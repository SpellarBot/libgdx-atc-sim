package com.atc.simulator.Display.DisplayModel;

import com.atc.simulator.flightdata.AircraftState;
import com.atc.simulator.flightdata.Track;
import com.atc.simulator.vectors.GeographicCoordinate;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Aircraft to be displayed in the display.
 * @author Luke Frisken
 */
public class DisplayAircraft extends SimpleModelInstanceProvider {
    private String aircraftID;
    private Track track;
    private DisplayPrediction prediction = null;

    /**
     * Constructor DisplayAircraft creates a new DisplayAircraft instance.
     *
     * @param track with this aircraft in it.
     */
    public DisplayAircraft(Track track)
    {
        this.track = track;
        update();


//        Vector3 screenPosition = cam.project(new Vector3(modelDrawVector));
//
//
//        String aircraftID = aircraftState.getAircraftID();
////                System.out.println("Screen Position: " + screenPosition + ", DisplayAircraft ID: " + aircraftID);
//
//        if (aircraftID.equals("QFA489"))
//        {
//            textPosition.x = screenPosition.x;
//            textPosition.y = screenPosition.y;
//        }
//
//        //if the aircraft is moving
//        if (velocity.length() > 0.00001)
//        {
//            GeographicCoordinate velocityEndPos = new GeographicCoordinate(position.add(velocity.mult(120))); //two minute velocity vector
//            aircraftStateVelocityModel = modelBuilder.createArrow(
//                    modelDrawVector,
//                    velocityEndPos.getModelDrawVector(depthAdjustment),
//                    new Material(ColorAttribute.createDiffuse(Color.GREEN)),
//                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
//            aircraftStateVelocityModelInstance = new ModelInstance(aircraftStateVelocityModel);

    }

    /**
     * Method setPrediction sets the current prediction associated with this aircraft.
     *
     * @param newPrediction the new prediction to be associated with this aircraft
     *
     */
    public void setPrediction(DisplayPrediction newPrediction)
    {
        prediction = newPrediction;
    }

    /**
     * Get the prediction associated with this aircraft.
     * @return DisplayPrediction
     */
    public DisplayPrediction getPrediction()
    {
        return prediction;
    }

    /**
     * Get the id of this aircraft.
     * @return String id of this aircraft
     */
    public String getAircraftID()
    {
        return aircraftID;
    }

    /**
     * Get the current AircraftState of this aircraft.
     * @return state of this aircraft.
     */
    public AircraftState getCurrentState()
    {
        return track.getLatest();
    }

    /**
     * Call to update the instance provided by this class.
     */
    @Override
    public void update() {
        super.update();

        AircraftState currentState = track.getLatest();

        this.aircraftID = currentState.getAircraftID();

        GeographicCoordinate position = currentState.getPosition();

        double depthAdjustment = -0.01;
        Vector3 modelDrawVector = position.getModelDrawVector(depthAdjustment);

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(
                0.0005f, 0.0005f, 0.0005f, 10, 10,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelInstance = new ModelInstance(model);
        modelInstance.transform.setTranslation(modelDrawVector.x, modelDrawVector.y, modelDrawVector.z);

        triggerOnInstanceUpdate(modelInstance);
    }
}