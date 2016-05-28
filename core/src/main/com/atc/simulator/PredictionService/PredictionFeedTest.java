package com.atc.simulator.PredictionService;

import com.atc.simulator.Display.PredictionFeedClient;
import com.atc.simulator.flightdata.AircraftState;
import com.atc.simulator.flightdata.ISO8601;
import com.atc.simulator.flightdata.Prediction;
import com.atc.simulator.vectors.GeographicCoordinate;
import com.atc.simulator.vectors.SphericalVelocity;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by Chris on 7/05/2016.
 *
 *  This is a test/familiarisation class for PredictionFeedServe's protocol buffer
 *  The buffer is called a PredictionMessage and contains: (valid 09/05/16)
 *      - String : AircraftID
 *      - Position[] : positionFuture (contains all the predicted future positions w/ (0)current, (1)first prediction, etc)
 *      -other optional data that can be altered later
 *
 *      The Position datatype contains three doubles, similar to the DebugDataFeedServe's protocol
 *
 * MODIFIED:
 * @version 0.3, CC 18/05/16
 * @author    Chris Coleman, 7191375
 */
public class PredictionFeedTest {
    //Test method to be comfortable with using PredictionFeedServe's protocol buffer
    public static void main(String[] arg) {

        //Create some Test data
       try{
           Prediction testPrediction = new Prediction("Test123", ISO8601.toCalendar(ISO8601.now()) );
           testPrediction.addPosToPrediction(new GeographicCoordinate(0,0,0));
           testPrediction.addPosToPrediction(new GeographicCoordinate(1,2,3));

            //Create Server/Client objects
            PredictionFeedServer testServer = new PredictionFeedServer();
            PredictionFeedClient testClient = new PredictionFeedClient();
            //Fill up the buffer a little bit
            for(int i = 0; i<10; i++)
                testServer.sendPredictionToServer(testPrediction);
            //Start the threads
            Thread tServer = new Thread(testServer, "ServerThread");
            tServer.start();
            Thread tClient = new Thread(testClient,"ClientThread");
            tClient.start();
            //Send messages on every entered item, loop out on 'q' being sent
            try{while(System.in.read() != 'q'){testServer.sendPredictionToServer(testPrediction);}}catch(IOException i){}

            //Close the Threads
            testServer.kill();
            testClient.kill();
       }catch(ParseException p){System.out.println("Clock failed");}

       System.out.println("Test Complete");
    }

}

