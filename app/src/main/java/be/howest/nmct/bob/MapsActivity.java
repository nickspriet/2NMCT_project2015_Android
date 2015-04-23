package be.howest.nmct.bob;


import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;


public class MapsActivity extends FragmentActivity
    implements OnMapReadyCallback
{
    private DrawerLayout _drawerLayout;
    private ImageButton btnAddParty;
    private MapFragment _mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_menu);
        btnAddParty = (ImageButton) findViewById(R.id.btnAddParty);


        //Add MapFragment (in code)
        if (savedInstanceState == null)
        {
            _mapFragment = MapFragment.newInstance();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, _mapFragment, "map")
                    .commit();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
    }
}