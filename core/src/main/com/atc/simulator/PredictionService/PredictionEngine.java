package com.atc.simulator.PredictionService;

import com.atc.simulator.DebugDataFeed.DataPlaybackListener;
import com.atc.simulator.RunnableThread;
import com.atc.simulator.flightdata.AircraftState;
import com.atc.simulator.flightdata.Prediction;
import com.atc.simulator.flightdata.SystemState;
import com.atc.simulator.vectors.GeographicCoordinate;

import java.util.ArrayList;

 /**
 * Basic engine, will receive system states, break them into AircraftStates, create predictions and push to the server
 *
 * @
 * PUBLIC FEATURES:
 * // Constructors
 *    Engine(PredictionFeedServerThread)
 * // Methods
 *    onSystemUpdate() - Listener Override, adds new AircraftStates to the Buffer
 *    run() - Thread of checking buffer and passing messages to server
 *    kill() - Clears the flag that the client thread runs off, letting the thread finish gracefully
 *    start() - Start new Thread
 *    makeNewPrediction(AircraftState) - Create a new prediction for the AircraftState given
 *
 * MODIFIED:
 * @version 0.1, CC 29/05/16, Initial creation/beginning of a general framework
 * @author    Chris Coleman, 7191375, Luke Frisken
 */
public class PredictionEngine implements RunnableThread, SystemStateDatabaseListener {
    //Internal settings
    private PredictionFeedServerThread myServer;
    private ArrayList<AircraftState> toBePredicted;
    //Thread definitions
    private boolean continueThread = true;  //Simple flag that dictates whether the Server threads will keep looping
    private Thread thread;  //Simple flag that dictates whether the Server threads will keep looping
    private final String threadName = "PredictionEngineThread";


    /**
     * Constructor, connect to the server and create a new arrayList
     * @param serv : The server I need to connect to
     */
    public PredictionEngine(PredictionFeedServerThread serv)
    {
        myServer = serv;
        toBePredicted = new ArrayList<AircraftState>();
    }

    /**
     * When the Data Client receives new data, it will call this. Putting all the new AircraftStates into the prediction buffer
     * @param systemState : The new state of the System
     */
    public synchronized void onSystemUpdate(SystemState systemState)
    {
        for(AircraftState aState : systemState.getAircraftStates())
            toBePredicted.add(aState);
    }

    /**
     * Threaded method; if there is data to be predicted, make one with the oldest data and send it to the server
     */
    public void run() {
        while (continueThread)
        {
            if(toBePredicted.size() > 0)
                makeNewPrediction(toBePredicted.remove(0));

            try { //Then sleep for a bit
                Thread.sleep(50);
            } catch (InterruptedException i){}
        }
    }

    /**
     * Private method that will create a new prediction and send it to the PredictionFeedServerThread
     */
    private synchronized void makeNewPrediction(AircraftState state)
    {
        ArrayList<GeographicCoordinate> predictionPositions = new ArrayList<GeographicCoordinate>();
        //Add the current position
        predictionPositions.add(state.getPosition());

        //Make a very simple prediction
        double tempAlt = state.getPosition().getAltitude();
        double tempLat = state.getPosition().getLatitude()+0.5;
        double tempLon = state.getPosition().getLongitude()+0.5;
        //Add it to the prediction
        predictionPositions.add(new GeographicCoordinate(tempAlt,tempLat,tempLon));

        //Add the ID and Time
        Prediction myPrediction = new Prediction(state.getAircraftID(), state.getTime(), predictionPositions);

        //Send the prediction to the server
        myServer.sendPrediction(myPrediction);
    }

    /**
     * Small method called too kill the server's threads when the have run through
     */
    public void kill()
    {
        continueThread = false;
    }

     /**
      * Join this thread.
      */
     @Override
     public void join() throws InterruptedException {
         thread.join();
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

     @Override
     public void systemStateUpdated(String[] aircraftIDs) {

     }
 }
