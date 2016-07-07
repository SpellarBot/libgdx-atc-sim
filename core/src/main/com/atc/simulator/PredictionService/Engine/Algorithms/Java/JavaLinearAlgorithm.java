package com.atc.simulator.PredictionService.Engine.Algorithms.Java;

import com.atc.simulator.flightdata.AircraftState;
import com.atc.simulator.flightdata.Prediction;
import com.atc.simulator.flightdata.Track;
import com.atc.simulator.vectors.GeographicCoordinate;
import com.atc.simulator.vectors.SphericalVelocity;
import pythagoras.d.Vector3;

import java.util.ArrayList;

/**
 * Created by luke on 6/07/16.
 */
public class JavaLinearAlgorithm extends JavaPredictionAlgorithm {
    /** @see JavaPredictionAlgorithm#makePrediction(Track) */
    @Override
    public Prediction makePrediction(Track aircraftTrack) {
        AircraftState state = aircraftTrack.getLatest();
        long startTime = state.getTime();
        GeographicCoordinate position = state.getPosition();
        SphericalVelocity velocity = state.getVelocity();
        ArrayList<AircraftState> predictedStates = new ArrayList<AircraftState>();

        int dt = 5000;
        int totalDT = 0;
        int n = 24;

        for (int i = 0; i < n; i++)
        {
            totalDT += dt;

            //make a linear prediction based on the current velocity
            GeographicCoordinate predictedPosition = new GeographicCoordinate(
                    position.add(velocity.mult(totalDT/1000))
            );

            AircraftState predictedState = new AircraftState(
                    state.getAircraftID(),
                    startTime+totalDT,
                    predictedPosition,
                    velocity,
                    0);
            predictedStates.add(predictedState);

        }

        Prediction prediction = new Prediction(state.getAircraftID(), startTime, predictedStates);
        return prediction;
    }
}