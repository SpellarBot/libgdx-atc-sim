package com.atc.simulator.flightdata;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Represents the state of a radar tracking/adsb/whatever aircraft tracking system at a given point in time.
 * @author Luke Frisken
 */
public class SystemState {
    private Calendar time;
    private ArrayList<AircraftState> aircraftStates;

    /**
     * Constructor for SystemState
     * @param time
     * @param aircraftStates
     */
    public SystemState(Calendar time, ArrayList<AircraftState> aircraftStates)
    {
        this.aircraftStates = aircraftStates;
        this.time = time;
    }


    /**
     * Get the time for this state
     * @return time
     */
    public Calendar getTime() {
        return time;
    }

    /**
     * Get the array of aircraft states for this system state.
     * @return the array of aircraft states
     */
    public ArrayList<AircraftState> getAircraftStates()
    {
        return this.aircraftStates;
    }
}