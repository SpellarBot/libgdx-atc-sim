package com.atc.simulator.DebugDataFeed;

import com.atc.simulator.DebugDataFeed.Scenarios.Scenario;
import com.atc.simulator.RunnableThread;
import com.atc.simulator.flightdata.ISO8601;
import com.atc.simulator.flightdata.SystemState;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by luke on 24/05/16.
 *
 * @author Luke Frisken
 */
public class DataPlaybackThread implements RunnableThread {
    private ArrayList<DataPlaybackListener> listeners;
    private int updateRate;
    private Scenario scenario;
    private String threadName;
    private Thread thread;
    private Calendar currentTime;
    private boolean continueThread;

    /**
     * Constructor for DataPlaybackThread
     * @param scenario the scenario to be played out by this thread
     * @param updateRate (in millisconds)
     */
    public DataPlaybackThread(Scenario scenario, int updateRate)
    {
        listeners = new ArrayList<DataPlaybackListener>();
        this.updateRate = updateRate;
        this.scenario = scenario;
        threadName = "DataPlayback";
        currentTime = (Calendar) scenario.getStartTime().clone();
        continueThread = true;
    }

    /**
     * Add a listener
     * @param listener
     */
    public void addListener(DataPlaybackListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a listener
     * @param listener
     */
    public void removeListener(DataPlaybackListener listener) {
        listeners.remove(listener);
    }

    /**
     * Trigger an onSystemState event on all the DataPlaybackListener listeners to this thread
     * @param systemState
     */
    private void triggerOnSystemUpdate(SystemState systemState)
    {
        for(DataPlaybackListener listener: listeners)
        {
            listener.onSystemUpdate(systemState);
        }
    }

    /**
     * The run method of this thread
     */
    @Override
    public void run() {
        Calendar endTime = scenario.getEndTime();
        while(continueThread)
        {
            try {
                //sleep for the desired update rate.
                //TODO: beware this is not precise tracking 1:1 of the time in the track
                //would be better to use the difference in the system clock, and interpolate
                //the values along the track in the Scenario. This is easier and better for performance for now.
                Thread.sleep(updateRate/10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            currentTime.add(Calendar.MILLISECOND, updateRate); //20 times speedup TODO: remove


            //finish if we have passed the end time
            if (currentTime.compareTo(endTime) > 0)
            {
                return;
            }

//            System.out.println("Debug CurrentTime");
//            System.out.println("CurrentTime: " + ISO8601.fromCalendar(currentTime));

            SystemState state = scenario.getState(currentTime);
//            System.out.println("StateTime: " + ISO8601.fromCalendar(state.getTime()));

            triggerOnSystemUpdate(state);
        }
    }

    /**
     * Start this thread
     */
    public void start()
    {
        if (thread == null)
        {
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

    /**
     * Kill this thread.
     */
    @Override
    public void kill() {
        continueThread = false;
    }
}