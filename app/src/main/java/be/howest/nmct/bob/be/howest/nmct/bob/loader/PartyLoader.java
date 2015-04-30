package be.howest.nmct.bob.be.howest.nmct.bob.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Base64;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.howest.nmct.bob.Party;

/**
 * Created by Nick on 28/04/2015.
 */
public class PartyLoader extends AsyncTaskLoader<Cursor>
{
    private static final String url = "http://student.howest.be/nick.spriet/BOB/parties3.php";
    private Cursor _cursor;
    private static Object lock = new Object();
    private List<Party> _lijstParties = null;


    private final String[] _columnNames = new String[]
            {
                    BaseColumns._ID,
                    Contract.PartyColumns.COLOMN_PARTY_NAME
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
                input = new URL(url).openStream();
                reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                _lijstParties = new ArrayList();

                //json file inlezen (rij per rij)
                reader.beginArray();
                while (reader.hasNext())
                {
                    reader.beginObject();
                    String name = "";
                    String description = "";
                    Bitmap picture = null;
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
                                    fromdate = format2.parse(reader.nextString());
                                }
                                catch (ParseException ex)
                                {
                                    Log.d("parseEX", ex.getMessage());
                                }
                                break;

                            case "PricePresale":
                            pricepresale = Double.parseDouble(reader.nextString());
                            break;

                            case "PriceAtTheDoor":
                                priceatthedoor = Double.parseDouble(reader.nextString());
                                break;

                            case "DiskJockey1":
                                diskjockey1 = reader.nextString();
                                break;

                            case "DiskJockey2":
                                diskjockey2 = reader.nextString();
                                break;

                            case "DiskJockey3":
                                diskjockey3 = reader.nextString();
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

                        Party p = new Party(name, description, picture, address, zipcode, city, fromdate, untildate, pricepresale, priceatthedoor, diskjockey1, diskjockey2, diskjockey3, latitude, longitude);
                        _lijstParties.add(p);
                    }

                    int id = 1;
                    MatrixCursor.RowBuilder row = matrixCursor.newRow();
                    row.add(id);
                    row.add(name);
                    id++;

                    reader.endObject();
                }
                reader.endArray();

                _cursor = matrixCursor;
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
                }

                try
                {
                    input.close();
                }
                catch (IOException ioEx)
                {
                }
            }
        }
    }
}