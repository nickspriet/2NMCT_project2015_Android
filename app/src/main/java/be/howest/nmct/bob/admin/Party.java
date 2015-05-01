package be.howest.nmct.bob.admin;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Nick on 28/04/2015.
 */
public class Party
{
    //properties
    private int ID;
    private String Name;
    private String Description;
    private byte[] Picture;
    private String Address;
    private String Zipcode;
    private String City;
    private Date FromDate;
    private Date UntilDate;
    private Double PricePresale;
    private Double PriceAtTheDoor;
    private String DiskJockey1;
    private String DiskJockey2;
    private String DiskJockey3;
    private Double Latitude;
    private Double Longitude;

    //constructor
    public Party(int id, String name, String description, byte[] picture, String address, String zipcode, String city, Date fromDate, Date untilDate, Double pricePresale, Double priceAtTheDoor, String diskJockey1, String diskJockey2, String diskJockey3, Double latitude, Double longitude)
    {
        ID = id;
        Name = name;
        Description = description;
        Picture = picture;
        Address = address;
        Zipcode = zipcode;
        City = city;
        FromDate = fromDate;
        UntilDate = untilDate;
        PricePresale = pricePresale;
        PriceAtTheDoor = priceAtTheDoor;
        DiskJockey1 = diskJockey1;
        DiskJockey2 = diskJockey2;
        DiskJockey3 = diskJockey3;
        Latitude = latitude;
        Longitude = longitude;
    }


    //getters
    public int getID()
    {
        return ID;
    }

    public String getName()
    {
        return Name;
    }

    public String getDescription()
    {
        return Description;
    }

    public byte[] getPicture()
    {
        return Picture;
    }

    public String getAddress()
    {
        return Address;
    }

    public String getZipcode()
    {
        return Zipcode;
    }

    public String getCity()
    {
        return City;
    }

    public Date getFromDate()
    {
        return FromDate;
    }

    public Date getUntilDate()
    {
        return UntilDate;
    }

    public Double getPricePresale()
    {
        return PricePresale;
    }

    public Double getPriceAtTheDoor()
    {
        return PriceAtTheDoor;
    }

    public String getDiskJockey1()
    {
        return DiskJockey1;
    }

    public String getDiskJockey2()
    {
        return DiskJockey2;
    }

    public String getDiskJockey3()
    {
        return DiskJockey3;
    }

    public Double getLatitude()
    {
        return Latitude;
    }

    public Double getLongitude()
    {
        return Longitude;
    }



    public static Party getPartyByID(int partyid)
    {
        return null;
    }
}
