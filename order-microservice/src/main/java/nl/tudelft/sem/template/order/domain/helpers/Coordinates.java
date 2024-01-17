package nl.tudelft.sem.template.order.domain.helpers;

public class Coordinates {
    public double latitude;
    public double longitude;

    /** Constructor for coordinates object.
     *
     * @param latitude E to W in degrees
     * @param longitude N to S in degrees
     */
    public Coordinates(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
