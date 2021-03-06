package com.atc.simulator.prediction_service.engine.algorithms.java;

import com.atc.simulator.config.ApplicationConfig;
import com.atc.simulator.flightdata.AircraftState;
import com.atc.simulator.flightdata.Prediction;
import com.atc.simulator.flightdata.Track;
import com.atc.simulator.vectors.GeographicCoordinate;

import java.util.ArrayList;

/**
 * Created by luke on 8/06/16.
 */
public class JavaPassthroughAlgorithm extends JavaPredictionAlgorithm {
    private static final boolean enableTimer = ApplicationConfig.getBoolean("settings.debug.algorithm-timer");

    @Override
    public Prediction makePrediction(Track aircraftTrack, Object algorithmState) {
        long start1=0, start2=0;
        if(enableTimer)
        {
            start1 = System.nanoTime();
            // maybe add here a call to a return to remove call up time, too.
            // Avoid optimization
            start2 = System.nanoTime();
        }

        AircraftState state = aircraftTrack.getLatest();
        GeographicCoordinate currentPosition = state.getPosition();
        ArrayList<AircraftState> predictedStates = new ArrayList<AircraftState>();
        //Add the current position
//        predictionPositions.add(state.getPosition());

        //Make a very simple prediction
//        double tempAlt = state.getPosition().getAltitude();
//        double tempLat = state.getPosition().getLatitude()+0.5;
//        double tempLon = state.getPosition().getLongitude()+0.5;
        //Add it to the prediction
//        predictionPositions.add(new GeographicCoordinate(tempAlt,tempLat,tempLon));
        Track predictedTrack = new Track(predictedStates);
        Prediction prediction = new Prediction(state.getAircraftID(),
                state.getTime(), state, null, predictedTrack, null, Prediction.State.STOPPED);
        if(enableTimer)
        {
            long stop = System.nanoTime();
            long diff = stop - 2*start2 + start1;
            System.out.println("JavaPassthroughAlgorithm work time: " + (((double) diff)/1000000.0) + " ms");
        }
        return prediction;
    }

    /**
     * Get a new state object for this algorithm.
     *
     * @return
     */
    @Override
    public Object getNewStateObject() {
        return null;
    }
}
