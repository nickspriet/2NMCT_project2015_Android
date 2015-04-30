package be.howest.nmct.bob;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;

import be.howest.nmct.bob.be.howest.nmct.bob.loader.Contract;
import be.howest.nmct.bob.be.howest.nmct.bob.loader.PartyLoader;


public class PartiesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private PartyAdapter _pAdapter;

    //constructor
    public PartiesFragment()
    {
        // Required empty public constructor
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


            TextView tvName = (TextView) holder.tvName;
            int columnnumber1 = cursor.getColumnIndex(Contract.PartyColumns.COLOMN_PARTY_NAME);
            tvName.setText(cursor.getString(columnnumber1));

            ImageView imgPicture = (ImageView) holder.imgPicture;
            int columnnumber2 = cursor.getColumnIndex(Contract.PartyColumns.COLOMN_PARTY_PICTURE);


            byte[] blob = cursor.getBlob(cursor.getColumnIndex(Contract.PartyColumns.COLOMN_PARTY_PICTURE));
            ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imgPicture.setImageBitmap(bitmap);
        }


        class PartyViewHolder
        {
            public TextView tvName = null;
            public ImageView imgPicture = null;

            //constructor
            PartyViewHolder(View rowParty)
            {
                tvName = (TextView) rowParty.findViewById(R.id.tvName);
                imgPicture = (ImageView) rowParty.findViewById(R.id.imgPicture);
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
                        Contract.PartyColumns.COLOMN_PARTY_NAME,
                        Contract.PartyColumns.COLOMN_PARTY_PICTURE
                };

        int[] viewIds = new int[]
                {
                        R.id.tvName,
                        R.id.imgPicture
                };

        //initialisatie adapter
        _pAdapter = new PartyAdapter(getActivity(), R.layout.row_party, null, columns, viewIds, 0);
        setListAdapter(_pAdapter);

        //activeer de loader
        getLoaderManager().initLoader(0, null, this);
    }

}
