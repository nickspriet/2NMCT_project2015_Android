package be.howest.nmct.bob.admin;

/**
 * Created by Nick on 02/05/2015.
 */
public class Bob
{
    //properties
    private int ID;
    private String LastName;
    private String FirstName;
    private String Car;
    private String Telephone;
    private int Seats;
    private double Latitude;
    private double Longitude;

    //constructor with params
    public Bob(int ID, String lastName, String firstName, String car, String telephone, int seats, double latitude, double longitude)
    {
        this.ID = ID;
        this.LastName = lastName;
        this.FirstName = firstName;
        this.Car = car;
        this.Telephone = telephone;
        this.Seats = seats;
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

    //empty constructor
    public Bob()
    {
    }

    //getters
    public int getID()
    {
        return ID;
    }

    public String getLastName()
    {
        return LastName;
    }

    public String getFirstName()
    {
        return FirstName;
    }

    public String getCar()
    {
        return Car;
    }

    public String getTelephone()
    {
        return Telephone;
    }

    public int getSeats()
    {
        return Seats;
    }

    public double getLatitude()
    {
        return Latitude;
    }

    public double getLongitude()
    {
        return Longitude;
    }


    //setters
    public void setID(int ID)
    {
        this.ID = ID;
    }

    public void setLastName(String lastName)
    {
        LastName = lastName;
    }

    public void setFirstName(String firstName)
    {
        FirstName = firstName;
    }

    public void setCar(String car)
    {
        Car = car;
    }

    public void setTelephone(String telephone)
    {
        Telephone = telephone;
    }

    public void setSeats(int seats)
    {
        Seats = seats;
    }

    public void setLatitude(double latitude)
    {
        Latitude = latitude;
    }

    public void setLongitude(double longitude)
    {
        Longitude = longitude;
    }
}
