package com.atc.simulator.Display.DisplayData;

import com.atc.simulator.Display.DisplayData.ModelInstanceProviders.ModelInstanceProvider;
import com.atc.simulator.Display.DisplayData.ModelInstanceProviders.ModelInstanceProviderMultiplexer;
import com.atc.simulator.Display.DisplayData.ModelInstanceProviders.PredictionModel;
import com.atc.simulator.flightdata.Prediction;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author Luke Frisken
 */
public class DisplayPrediction extends Prediction implements Disposable, ModelInstanceProviderMultiplexer {
    private HashMap<String, ModelInstanceProvider> models;
    private DisplayAircraft aircraft;
    /**
     * Constructor DisplayPrediction creates a new DisplayPrediction instance.
     *
     * @param aircraft the aircraft this prediction belongs to
     * @param prediction of type Prediction
     */
    public DisplayPrediction(DisplayAircraft aircraft, Prediction prediction) {
        super(prediction.getAircraftID(), prediction.getPredictionTime(), prediction.getAircraftStates());
        this.aircraft = aircraft;
        models = new HashMap<String, ModelInstanceProvider>();
        createModels();
    }

    private void createModels()
    {

        models.put("PredictionLine", new PredictionModel(this));
    }

    public DisplayAircraft getAircraft()
    {
        return aircraft;
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        for (ModelInstanceProvider model : models.values())
        {
            model.dispose();
        }
    }

    @Override
    public Collection<ModelInstanceProvider> getInstanceProviders() {
        return models.values();
    }

    /**
     * Update this prediction with new prediction values.
     * and update the model instances provided by this object.
     * @param newPrediction
     */
    public void update(Prediction newPrediction)
    {
        this.copyData(newPrediction);
        update();
    }

    /**
     * Call to update the instances provided by this multiplexer.
     */
    @Override
    public void update() {
        for (ModelInstanceProvider model : models.values())
        {
            model.update();
        }
    }
}