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

import be.howest.nmct.bob.admin.Bob;
import be.howest.nmct.bob.admin.BobAdmin;

/**
 * Created by Nick on 02/05/2015.
 */
public class BobLoader extends AsyncTaskLoader<Cursor>
{
    private static final String serverURL = "http://student.howest.be/nick.spriet/BOB/";
    private Cursor _cursor;
    private static Object lock = new Object();
    private List<Bob> _lijstBobs = null;


    private final String[] _columnNames = new String[]
            {
                    BaseColumns._ID,
                    Contract.BobColumns.COLUMN_BOB_ID,
                    Contract.BobColumns.COLUMN_BOB_LASTNAME,
                    Contract.BobColumns.COLUMN_BOB_FIRSTNAME,
                    Contract.BobColumns.COLUMN_BOB_CAR,
                    Contract.BobColumns.COLUMN_BOB_TELEPHONE,
                    Contract.BobColumns.COLUMN_BOB_SEATS,
                    Contract.BobColumns.COLUMN_BOB_LATITUDE,
                    Contract.BobColumns.COLUMN_BOB_LONGITUDE
            };


    //constructor
    public BobLoader(Context context)
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
                input = new URL(serverURL + "bobs.php").openStream();
                reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                _lijstBobs = new ArrayList();

                //json file inlezen (rij per rij)
                reader.beginArray();
                while (reader.hasNext())
                {
                    reader.beginObject();
                    int bobid = 0;
                    String lastname = "";
                    String firstname = "";
                    String car = "";
                    String telephone = "";
                    int seats = 0;
                    double latitude = 0.0;
                    double longitude = 0.0;

                    while (reader.hasNext())
                    {
                        String nextName = reader.nextName();

                        switch (nextName)
                        {
                            case "ID":
                                bobid = reader.nextInt();
                                break;

                            case "LastName":
                                lastname = reader.nextString();
                                break;

                            case "FirstName":
                                firstname = reader.nextString();
                                break;

                            case "Car":
                                car = reader.nextString();
                                break;

                            case "Telephone":
                                telephone = reader.nextString();
                                break;

                            case "Seats":
                                if (reader.peek() == JsonToken.STRING) seats = Integer.parseInt(reader.nextString());
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
                    Bob b = new Bob(bobid, lastname, firstname, car, telephone, seats, latitude, longitude);
                    _lijstBobs.add(b);

                    int id = 1;
                    MatrixCursor.RowBuilder row = matrixCursor.newRow();
                    row.add(id);
                    row.add(bobid);
                    row.add(lastname);
                    row.add(firstname);
                    row.add(car);
                    row.add(telephone);
                    row.add(seats);
                    row.add(latitude);
                    row.add(longitude);
                    id++;

                    reader.endObject();
                }
                reader.endArray();

                _cursor = matrixCursor;
                BobAdmin.setBobs(_lijstBobs);
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
}
