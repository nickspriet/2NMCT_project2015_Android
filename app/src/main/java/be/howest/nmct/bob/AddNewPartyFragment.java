package be.howest.nmct.bob;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class AddNewPartyFragment extends Fragment
{
    //constructor
    public AddNewPartyFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_party, container, false);
    }


    public static AddNewPartyFragment newInstance(Location location)
    {
        AddNewPartyFragment anpFragment = new AddNewPartyFragment();

        return anpFragment;
    }


}
