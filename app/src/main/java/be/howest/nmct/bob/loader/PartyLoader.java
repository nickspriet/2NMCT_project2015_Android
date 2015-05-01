package be.howest.nmct.bob.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.howest.nmct.bob.admin.Party;
import be.howest.nmct.bob.admin.PartyAdmin;

/**
 * Created by Nick on 28/04/2015.
 */
public class PartyLoader extends AsyncTaskLoader<Cursor>
{
    private static final String serverURL = "http://student.howest.be/nick.spriet/BOB/";
    private Cursor _cursor;
    private static Object lock = new Object();
    private List<Party> _lijstParties = null;


    private final String[] _columnNames = new String[]
            {
                    BaseColumns._ID,
                    Contract.PartyColumns.COLUMN_PARTY_ID,
                    Contract.PartyColumns.COLUMN_PARTY_NAME,
                    Contract.PartyColumns.COLUMN_PARTY_DESCRIPTION,
                    Contract.PartyColumns.COLUMN_PARTY_PICTURE,
                    Contract.PartyColumns.COLUMN_PARTY_ADDRESS,
                    Contract.PartyColumns.COLUMN_PARTY_ZIPCODE,
                    Contract.PartyColumns.COLUMN_PARTY_CITY,
                    Contract.PartyColumns.COLUMN_PARTY_FROMDATE,
                    Contract.PartyColumns.COLUMN_PARTY_UNTILDATE,
                    Contract.PartyColumns.COLUMN_PARTY_PRICEPRESALE,
                    Contract.PartyColumns.COLUMN_PARTY_PRICEATTHEDOOR,
                    Contract.PartyColumns.COLUMN_PARTY_DISKJOCKEY1,
                    Contract.PartyColumns.COLUMN_PARTY_DISKJOCKEY2,
                    Contract.PartyColumns.COLUMN_PARTY_DISKJOCKEY3,
                    Contract.PartyColumns.COLUMN_PARTY_LATITUDE,
                    Contract.PartyColumns.COLUMN_PARTY_LONGITUDE,
            };


    //constructor
    public PartyLoader(Context context)
    {
        super(context);
    }

    //is er reeds data ingeladen en/of is de content gewijzigd ?
    @Override
    protected void onStartLoading()
    {
        // If we currently have a result available, deliver it immediately.
        if (_cursor != null) deliverResult(_cursor);

        // If the data has changed since the last time it was loaded or is not currently available, start a load.
        if (takeContentChanged() || _cursor == null) forceLoad();
    }

    //This is where the bulk of our work is done.  This function is called in a background thread and should generate a new set of data to be published by the loader
    @Override
    public Cursor loadInBackground()
    {
        if (_cursor == null) loadCursor();

        return _cursor;
    }


    private void loadCursor()
    {
        synchronized (lock)
        {
            if (_cursor != null) return;

            MatrixCursor matrixCursor = new MatrixCursor(_columnNames);
            InputStream input = null;
            JsonReader reader = null;

            try
            {
                input = new URL(serverURL + "parties3.php").openStream();
                reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                _lijstParties = new ArrayList();

                //json file inlezen (rij per rij)
                reader.beginArray();
                while (reader.hasNext())
                {
                    reader.beginObject();
                    int partyid = 0;
                    String name = "";
                    String description = "";
                    byte[] picture = null;
                    String address = "";
                    String zipcode = "";
                    String city = "";
                    Date fromdate = null;
                    Date untildate = null;
                    Double pricepresale = 0.0;
                    Double priceatthedoor = 0.0;
                    String diskjockey1 = "";
                    String diskjockey2 = "";
                    String diskjockey3 = "";
                    Double latitude = 0.0;
                    Double longitude = 0.0;

                    while (reader.hasNext())
                    {
                        String nextName = reader.nextName();

                        switch (nextName)
                        {
                            case "ID":
                                partyid = reader.nextInt();

                                //picture ophalen van de server adhv het id (bv. 0.png, 1.png, ...)
                                picture = getPictureFromPartyID(partyid);
                                break;

                            case "Name":
                                name = reader.nextString();
                                break;

                            case "Description":
                                description = reader.nextString();
                                break;

                            /*case "Picture":
                                byte[] decodedString = Base64.decode(reader.nextString().getBytes(), Base64.DEFAULT);
                                picture = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                break;*/

                            case "Address":
                                address = reader.nextString();
                                break;

                            case "Zipcode":
                                zipcode = reader.nextString();
                                break;

                            case "City":
                                city = reader.nextString();
                                break;

                            case "FromDate":
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try
                                {
                                    fromdate = format.parse(reader.nextString());
                                }
                                catch (ParseException ex)
                                {
                                    Log.d("parseEX", ex.getMessage());
                                }
                                break;

                            case "UntilDate":
                                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try
                                {
                                    untildate = format2.parse(reader.nextString());
                                }
                                catch (ParseException ex)
                                {
                                    Log.d("parseEX", ex.getMessage());
                                }
                                break;

                            case "PricePresale":
                                if (reader.peek() == JsonToken.STRING) pricepresale = Double.parseDouble(reader.nextString());
                                else  reader.skipValue();
                                break;

                            case "PriceAtTheDoor":
                                priceatthedoor = Double.parseDouble(reader.nextString());
                                break;

                            case "DiskJockey1":
                                if (reader.peek() == JsonToken.STRING) diskjockey1 = reader.nextString();
                                else  reader.skipValue();
                                break;

                            case "DiskJockey2":
                                if (reader.peek() == JsonToken.STRING) diskjockey2 = reader.nextString();
                                else  reader.skipValue();
                                break;

                            case "DiskJockey3":
                                if (reader.peek() == JsonToken.STRING) diskjockey3 = reader.nextString();
                                else  reader.skipValue();
                                break;

                            case "Latitude":
                                latitude = reader.nextDouble();
                                break;

                            case "Longitude":
                                longitude = reader.nextDouble();
                                break;

                            default:
                                reader.skipValue();
                                break;
                        }
                    }
                    Party p = new Party(partyid, name, description, picture, address, zipcode, city, fromdate, untildate, pricepresale, priceatthedoor, diskjockey1, diskjockey2, diskjockey3, latitude, longitude);
                    _lijstParties.add(p);

                    int id = 1;
                    MatrixCursor.RowBuilder row = matrixCursor.newRow();
                    row.add(id);
                    row.add(partyid);
                    row.add(name);
                    row.add(description);
                    row.add(picture);
                    row.add(address);
                    row.add(zipcode);
                    row.add(city);
                    row.add(fromdate);
                    row.add(untildate);
                    row.add(pricepresale);
                    row.add(priceatthedoor);
                    row.add(diskjockey1);
                    row.add(diskjockey2);
                    row.add(diskjockey3);
                    row.add(latitude);
                    row.add(longitude);
                    id++;

                    reader.endObject();
                }
                reader.endArray();

                _cursor = matrixCursor;
                PartyAdmin.setParties(_lijstParties);
            }
            catch (IOException ioEx)
            {
                ioEx.printStackTrace();
            }
            finally
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioEx)
                {
                    ioEx.printStackTrace();
                }

                try
                {
                    input.close();
                }
                catch (IOException ioEx)
                {
                    ioEx.printStackTrace();
                }
            }
        }
    }

    private byte[] getPictureFromPartyID(int partyid) throws IOException
    {
        URL imageURL = new URL(serverURL + "images/" + partyid + ".jpg");
        Bitmap bmPicture = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmPicture.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();    }
}