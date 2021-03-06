package com.atc.simulator.vectors;

import pythagoras.d.Matrix3;
import pythagoras.d.Vector;
import pythagoras.d.Vector3;

/**
 * Created by luke on 9/04/16.
 * See: http://mathworld.wolfram.com/SphericalCoordinates.html
 *
 * The normal range for the variables is as follows:
 *
 * r (0 -&gt; infinity)
 * theta (0 -&gt; 2PI)
 * phi (0 -&gt; PI)
 *
 * @author Luke Frisken
 */
public class SphericalCoordinate extends Vector3 {
    private static final double TWOPI = Math.PI*2.0;

    /**
     * Get an equivalent SphericalCoordinate from a cartesian coordinate vector.
     * @param cv cartesian coordinate vector
     * @return Spherical coordinate
     */
    public static SphericalCoordinate fromCartesian(Vector3 cv)
    {
        double r = Math.sqrt(cv.x*cv.x + cv.y*cv.y + cv.z*cv.z);
        double theta = Double.NaN;

        //ensuring the inverse tangent is in the correct quadrant
        if (cv.x > 0)
        {
            theta = Math.atan(cv.y/cv.x);
        } else if (cv.x < 0) {
            if (cv.y > 0)
            {
                theta = Math.atan(cv.y/cv.x)+Math.PI;
            } else if (cv.y < 0)
            {
                theta = Math.atan(cv.y/cv.x)-Math.PI;
            } else {
                theta = Math.PI;
            }
        } else if (cv.x == 0)
        {
            if (cv.y > 0) {
                theta = Math.PI;
            } else if (cv.y < 0)
            {
                theta = -Math.PI;
            } else {
                theta = Double.NaN;
            }

        }

        double phi = Math.acos(cv.z/r);
        return (new SphericalCoordinate(r,theta,phi)).rectifyBounds();
    }

    public SphericalCoordinate(SphericalCoordinate other)
    {
        super(other);
    }
    public SphericalCoordinate(Vector3 other)
    {
        super(other);
    }
    public SphericalCoordinate(double r, double theta, double phi) {
        this.x = r;
        this.y = theta;
        this.z = phi;
    }

    /**
     * Get Radius Component
     * @return
     */
    public double getR() {
        return this.x;
    }

    /**
     * Set the Radius component
     */
    public void setR(double r)
    {
        this.x = r;
    }

    /**
     * Get Theta Component
     * @return
     */
    public double getTheta() {
        return this.y;
    }

    /**
     * Set the Theta Component
     */
    public void setTheta(double theta) {
        this.y = theta;
    }

    /**
     * Get Phi Component
     * @return the phi component
     */
    public double getPhi() {
        return this.z;
    }

    /**
     * Set the Phi Component
     */
    public void setPhi(double phi) {
        this.z = phi;
    }


    /**
     * Transform this SphericalCoordinate into a new one
     * which has the equivalent value, but is within the
     * normal boundary conditions:
     *
     * r (0 -&gt; infinity)
     * theta (0 -&gt; 2PI)
     * phi (0 -&gt; PI)
     * TODO: this looks kind of broken
     *
     * @return rectified SphericalCoordinate
     */
    public SphericalCoordinate rectifyBounds()
    {
//        System.out.println("before rectification" + this);
        double x = this.x;
        double y = this.y;
        double z = this.z;

        while (z < 0) {
            z += TWOPI;
        }

        while (z > TWOPI) {
            z -= TWOPI;
        }

        if (z > Math.PI) {
            z = (TWOPI - z);
            y += Math.PI;
        } else {
            if (z < 0) {
                z = -(TWOPI + z);
                y += Math.PI;
            }
        }

        while (y > TWOPI) {
            y -= TWOPI;
        }

        while (y < 0) {
            y += TWOPI;
        }



        return new SphericalCoordinate(x, y, z);
    }

    /**
     * get the equivalent cartesian coordinate vector of this spherical coordinate.
     * @return
     */
    public Vector3 getCartesian() {
        SphericalCoordinate rectified = this.rectifyBounds();
        double r = rectified.x;
        double theta = rectified.y;
        double phi = rectified.z;
        return new Vector3(
                (float) (r * Math.cos(theta) * Math.sin(phi)),
                (float) (r * Math.sin(theta) * Math.sin(phi)),
                (float) (r * Math.cos(phi)));
    }

    /**
     * Get the cartesian libgdx vector3 required for 3D drawing.
     * @return the cartesian draw vector
     */
    public com.badlogic.gdx.math.Vector3 getCartesianDrawVector()
    {
//        System.out.println("Coords after transform R:"+this.getR()+" Theta:"+this.getTheta()+" Phi:" + this.getPhi());
//        Vector3 cartesian = new SphericalCoordinate(this.x, this.z, this.y).getCartesian(); //for some reason I need to reverse this!
        Vector3 cartesian = this.getCartesian();
        return new com.badlogic.gdx.math.Vector3((float) cartesian.x, (float) -cartesian.z, (float) cartesian.y);
    }

    /**
     * Get a draw vector, but with the radius set to 0.99 to be just below the surface of a textured
     * model of the earth.
     * @return the cartesian draw vector
     */
    public com.badlogic.gdx.math.Vector3 getModelDrawVector()
    {
        GeographicCoordinate adjustedCoordinate = new GeographicCoordinate(this);
        /*
        set radius to 0.99 to get the draw vector to be beneath the surface of the planet
        which sits at 1.00. As we are looking at the planet from the inside, this ensures
        that this coordinate is drawn on top.
        */
        adjustedCoordinate.x = 0.99;
        return adjustedCoordinate.getCartesianDrawVector();
    }

    /**
     * Get a draw vector, but with the radius set to 0.99 to be just below the surface of a textured
     * model of the earth.
     *
     * @param adjust adjust the radius (draw depth)
     * @return the cartesian draw vector
     */
    public com.badlogic.gdx.math.Vector3 getModelDrawVector(double adjust)
    {
        GeographicCoordinate adjustedCoordinate = new GeographicCoordinate(this);
        /*
        set radius to 0.99 to get the draw vector to be beneath the surface of the planet
        which sits at 1.00. As we are looking at the planet from the inside, this ensures
        that this coordinate is drawn on top.
        */
        adjustedCoordinate.x = 0.99 + adjust;
        return adjustedCoordinate.getCartesianDrawVector();
    }

    /**
     * Get the cartesian unit vector for R
     * @return
     */
    public Vector3 rCartesianUnitVector()
    {
        SphericalCoordinate rectified = this.rectifyBounds();
        double theta = rectified.y;
        double phi = rectified.z;
        return new Vector3(
                (float) (Math.cos(theta) * Math.sin(phi)),
                (float) (Math.sin(theta) * Math.sin(phi)),
                (float) (Math.cos(phi)));
    }

    /**
     * Get the cartesian unit vector for Phi
     * @return
     */
    public Vector3 phiCartesianUnitVector()
    {
        SphericalCoordinate rectified = this.rectifyBounds();
        double theta = rectified.y;
        double phi = rectified.z;

        return new Vector3(
                Math.cos(phi)*Math.cos(theta),
                Math.cos(phi)*Math.sin(theta),
                -Math.sin(phi)
        );
    }

    /**
     * Get the cartesian unit vector for theta
     * @return
     */
    public Vector3 thetaCartesianUnitVector()
    {
        SphericalCoordinate rectified = this.rectifyBounds();
        double theta = rectified.y;

        return new Vector3(
                -Math.sin(theta),
                Math.cos(theta),
                0
        );
    }

    /**
     * Cartesian distance between this and another coordinate
     * @param other
     * @return cartesian distance in metres
     */
    public double cartesianDistance(SphericalCoordinate other)
    {
        return this.getCartesian().subtract(other.getCartesian()).length();
    }
}
