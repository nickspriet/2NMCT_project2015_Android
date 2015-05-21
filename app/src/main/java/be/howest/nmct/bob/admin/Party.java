package be.howest.nmct.bob.admin;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;
import java.util.List;

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
    private String FromDate;
    private String UntilDate;
    private Double PricePresale;
    private Double PriceAtTheDoor;
    private String DiskJockey1;
    private String DiskJockey2;
    private String DiskJockey3;
    private Double Latitude;
    private Double Longitude;

    private Uri ImgURI;

    //constructor
    public Party(int id, String name, String description, byte[] picture, String address, String zipcode, String city, String fromDate, String untilDate, Double pricePresale, Double priceAtTheDoor, String diskJockey1, String diskJockey2, String diskJockey3, Double latitude, Double longitude)
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


    //empty contstructor
    public Party()
    {
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

    public String getFromDate()
    {
        return FromDate;
    }

    public String getUntilDate()
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


    //setters
    public void setID(int ID)
    {
        this.ID = ID;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public void setPicture(byte[] picture)
    {
        Picture = picture;
    }

    public void setAddress(String address)
    {
        Address = address;
    }

    public void setZipcode(String zipcode)
    {
        Zipcode = zipcode;
    }

    public void setCity(String city)
    {
        City = city;
    }

    public void setFromDate(String fromDate)
    {
        FromDate = fromDate;
    }

    public void setUntilDate(String untilDate)
    {
        UntilDate = untilDate;
    }

    public void setPricePresale(Double pricePresale)
    {
        PricePresale = pricePresale;
    }

    public void setPriceAtTheDoor(Double priceAtTheDoor)
    {
        PriceAtTheDoor = priceAtTheDoor;
    }

    public void setDiskJockey1(String diskJockey1)
    {
        DiskJockey1 = diskJockey1;
    }

    public void setDiskJockey2(String diskJockey2)
    {
        DiskJockey2 = diskJockey2;
    }

    public void setDiskJockey3(String diskJockey3)
    {
        DiskJockey3 = diskJockey3;
    }

    public void setLatitude(Double latitude)
    {
        Latitude = latitude;
    }

    public void setLongitude(Double longitude)
    {
        Longitude = longitude;
    }



    public Uri getImgURI() { return ImgURI; }
    public void setImgURI(Uri imgURI) { ImgURI = imgURI; }


}
