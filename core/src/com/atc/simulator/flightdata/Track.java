package com.atc.simulator.flightdata;

import com.atc.simulator.vectors.GeographicCoordinate;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by luke on 7/04/16.
 * Represents a continuous track of an aircraft as it flies through the air, with regular
 * TrackEntry's representing the state of the aircraft for each point in time.
 */
public class Track extends ArrayList<TrackEntry> {

    /**
     * Read in the track data from a CSV file generated by xplage
     * (http://www.chriskern.net/code/xplaneToGoogleEarth.html)
     * @param file
     * @return
     */
    public static Track readFromCSVFile(FileHandle file)
    {
        Track track = new Track();


        double latitude;
        double longitude;
        Calendar time;

        String csv_string = file.readString();
        String[] lines = csv_string.split(System.getProperty("line.separator"));

        for (String line : lines)
        {

            List<String> line_values = Arrays.asList(line.split(","));
            try {
                time = ISO8601.toCalendar(line_values.get(0));

                //Funnily enough, the latitude and longitude are in the oposite order
                //to what you usually find.
                longitude = Math.toRadians(Double.parseDouble(line_values.get(1)));
                latitude = Math.toRadians(Double.parseDouble(line_values.get(2)));
                GeographicCoordinate position = new GeographicCoordinate(0.99, latitude, longitude);
                track.add(
                        new TrackEntry(time, position)
                );

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        return track;
    }

    /**
     * Generate a GL_LINES model of the track
     * @return
     */
    public Model getModel(){
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part(
                "track",
                GL20.GL_LINES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked,
                new Material());
        builder.setColor(Color.RED);
        int jump = 1;
        for(int i = jump; i < this.size(); i+=jump)
        {
            TrackEntry prev_entry = this.get(i-jump);
            TrackEntry entry = this.get(i);
            System.out.println(entry.getPosition());
            Vector3 prev_pos = prev_entry.getPosition().getCartesianDrawVector();
            Vector3 pos = entry.getPosition().getCartesianDrawVector();
            System.out.println(prev_pos.len());
            System.out.println(pos.len());
            System.out.println(pos);
            builder.line(prev_pos, pos);
        }

        return modelBuilder.end();
    }

}
