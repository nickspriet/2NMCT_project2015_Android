package be.howest.nmct.bob.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import be.howest.nmct.bob.admin.PartyBob;
import be.howest.nmct.bob.admin.PartyBobAdmin;

/**
 * Created by Nick on 03/05/2015.
 */
public class PartyBobLoader extends AsyncTaskLoader<Cursor>
    {
    private static final String serverURL = "http://student.howest.be/nick.spriet/BOB/";
    private Cursor _cursor;
    private static Object lock = new Object();
    private List<PartyBob> _lijstPartyBobs = null;


    private final String[] _columnNames = new String[]
            {
                    BaseColumns._ID,
                    Contract.PartyBobColumns.COLUMN_PARTYBOB_PARTYID,
                    Contract.PartyBobColumns.COLUMN_PARTYBOB_BOBID
            };


    //constructor
    public PartyBobLoader(Context context)
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
                input = new URL(serverURL + "partybobs.php").openStream();
                reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                _lijstPartyBobs = new ArrayList();

                //json file inlezen (rij per rij)
                reader.beginArray();
                while (reader.hasNext())
                {
                    reader.beginObject();
                    int partyid = 0;
                    int bobid = 0;

                    while (reader.hasNext())
                    {
                        String nextName = reader.nextName();

                        switch (nextName)
                        {
                            case "PartyID":
                                partyid = reader.nextInt();
                                break;

                            case "BobID":
                                bobid = reader.nextInt();
                                break;

                            default:
                                reader.skipValue();
                                break;
                        }
                    }
                    PartyBob b = new PartyBob(partyid, bobid);
                    _lijstPartyBobs.add(b);

                    int id = 1;
                    MatrixCursor.RowBuilder row = matrixCursor.newRow();
                    row.add(id);
                    row.add(partyid);
                    row.add(bobid);
                    id++;

                    reader.endObject();
                }
                reader.endArray();

                _cursor = matrixCursor;
                PartyBobAdmin.setPartyBobs(_lijstPartyBobs);
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