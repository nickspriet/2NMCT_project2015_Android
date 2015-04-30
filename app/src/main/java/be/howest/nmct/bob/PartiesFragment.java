package be.howest.nmct.bob;


import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import be.howest.nmct.bob.helper.CircularImageHelper;
import be.howest.nmct.bob.loader.Contract;
import be.howest.nmct.bob.loader.PartyLoader;


public class PartiesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private PartyAdapter _pAdapter;


    //constructor
    public PartiesFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parties, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new PartyLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        _pAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        _pAdapter.swapCursor(null);
    }


    //visualiseer de data afkomstig vn de cursor
    class PartyAdapter extends SimpleCursorAdapter
    {
        //default constructor
        public PartyAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags)
        {
            super(context, layout, c, from, to, flags);
        }


        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            super.bindView(view, context, cursor);

            PartyViewHolder holder = (PartyViewHolder) view.getTag();
            if (holder == null)
            {
                holder = new PartyViewHolder(view);
                view.setTag(holder);
            }

            TextView tvName = holder.tvName;
            int columnnumber1 = cursor.getColumnIndex(Contract.PartyColumns.COLUMN_PARTY_NAME);
            tvName.setText(cursor.getString(columnnumber1));

            ImageView imgPicture = holder.imgPicture;
            int columnnumber4 = cursor.getColumnIndex(Contract.PartyColumns.COLUMN_PARTY_PICTURE);
            byte[] blob = cursor.getBlob(columnnumber4);
            Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            imgPicture.setImageBitmap(CircularImageHelper.getCroppedBitmap(bitmap, 200));

            TextView tvAddressZipcodeCity = holder.tvZipcodeCity;
            int columnnumber6 = cursor.getColumnIndex(Contract.PartyColumns.COLUMN_PARTY_ZIPCODE);
            int columnnumber7 = cursor.getColumnIndex(Contract.PartyColumns.COLUMN_PARTY_CITY);
            tvAddressZipcodeCity.setText(cursor.getString(columnnumber6) + " " + cursor.getString(columnnumber7));
        }


        class PartyViewHolder
        {
            public ImageView imgPicture = null;
            public TextView tvName = null;
            public TextView tvZipcodeCity = null;

            //constructor
            PartyViewHolder(View rowParty)
            {
                imgPicture = (ImageView) rowParty.findViewById(R.id.imgPicture);
                tvName = (TextView) rowParty.findViewById(R.id.tvName);
                tvZipcodeCity = (TextView) rowParty.findViewById(R.id.tvZipcodeCity);
            }
        }
    }


    //adapter initialiseren en aan listview koppelen
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        String[] columns = new String[]
                {
                        Contract.PartyColumns.COLUMN_PARTY_ID,
                        Contract.PartyColumns.COLUMN_PARTY_NAME,
                        Contract.PartyColumns.COLUMN_PARTY_PICTURE
                };

        int[] viewIds = new int[]
                {
                        R.id.tvName,
                        //R.id.imgPicture
                };

        //initialisatie adapter
        _pAdapter = new PartyAdapter(getActivity(), R.layout.row_party, null, columns, viewIds, 0);
        setListAdapter(_pAdapter);

        //activeer de loader
        getLoaderManager().initLoader(0, null, this);
    }
}
