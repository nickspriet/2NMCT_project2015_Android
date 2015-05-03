package be.howest.nmct.bob;


import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import be.howest.nmct.bob.admin.Bob;
import be.howest.nmct.bob.admin.BobAdmin;
import be.howest.nmct.bob.admin.Party;
import be.howest.nmct.bob.admin.PartyBob;
import be.howest.nmct.bob.admin.PartyBobAdmin;
import be.howest.nmct.bob.loader.BobLoader;
import be.howest.nmct.bob.loader.Contract;
import be.howest.nmct.bob.loader.PartyBobLoader;


public class BobsAtPartyFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private BobAdapter _bAdapter;
    private RecyclerView rvBobsAtParty;
    private RecyclerView.LayoutManager _layoutManager;
    private List<Bob> _dataSet;
    private int _partyID;

    public BobsAtPartyFragment()
    {
        // Required empty public constructor
    }

    public BobsAtPartyFragment(int partyid)
    {
        _partyID = partyid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bobs_at_party, container, false);

        rvBobsAtParty = (RecyclerView) v.findViewById(R.id.rvBobsAtParty);

        return v;
    }

    //visualize data from loader with this Adapter class
    class BobAdapter extends RecyclerView.Adapter<BobAdapter.BobViewHolder>
    {
        // Provide a suitable constructor (depends on the kind of dataset)
        BobAdapter(List<Bob> myDataset)
        {
            _dataSet = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public BobViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bob, parent, false);

            // set the view's size, margins, paddings and layout parameters
            BobViewHolder bobViewHolder = new BobViewHolder(v);

            return  bobViewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(BobViewHolder bobViewHolder, int position)
        {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            bobViewHolder.tvLastName.setText(_dataSet.get(position).getLastName().substring(0, 1) + ".");
            bobViewHolder.tvFirstName.setText(_dataSet.get(position).getFirstName());
            bobViewHolder.tvCar.setText(_dataSet.get(position).getCar());
            bobViewHolder.tvTelephone.setText(_dataSet.get(position).getTelephone());
            bobViewHolder.tvSeats.setText(String.valueOf(_dataSet.get(position).getSeats()) + " seats");
        }


        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount()
        {
            return _dataSet.size();
        }


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class BobViewHolder extends RecyclerView.ViewHolder
        {
            public TextView tvLastName = null;
            public TextView tvFirstName = null;
            public TextView tvCar = null;
            public TextView tvTelephone = null;
            public TextView tvSeats = null;

            public BobViewHolder(View rowBob)
            {
                // each data item is just a string in this case
                super(rowBob);

                tvLastName = (TextView) rowBob.findViewById(R.id.tvLastName);
                tvFirstName = (TextView) rowBob.findViewById(R.id.tvFirstName);
                tvCar = (TextView) rowBob.findViewById(R.id.tvCar);
                tvTelephone = (TextView) rowBob.findViewById(R.id.tvTelephone);
                tvSeats = (TextView) rowBob.findViewById(R.id.tvSeats);

                tvTelephone.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvTelephone.getText().toString()));
                        startActivity(callIntent);
                    }
                });
            }
        }
    }
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        AsyncTaskLoader<Cursor> loader;

        if (id == 0) loader = new PartyBobLoader(getActivity());
        else loader = new BobLoader(getActivity());

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if (loader instanceof BobLoader)
        {
            List<PartyBob> lijstPartybobs = PartyBobAdmin.getPartyBobs();
            List<Bob> lijstBobs = BobAdmin.getBobsFromPartyID(_partyID);
            _bAdapter = new BobAdapter(lijstBobs);
            rvBobsAtParty.setAdapter(_bAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        new PartyBobLoader(getActivity());
        new BobLoader(getActivity());
    }


    //initialize adaper and bind to recyclerview
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //add layoutmanager to recyclerview
        _layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rvBobsAtParty.setLayoutManager(_layoutManager);

        //create the loaders
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
    }
}
