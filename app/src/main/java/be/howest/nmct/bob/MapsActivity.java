package be.howest.nmct.bob;


import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity
    implements OnMapReadyCallback
{
    private DrawerLayout _drawerLayout;
    private ImageButton btnAddParty;
    private MapFragment _mapFragment;

    private LocationManager _locationManager;
    private LocationListener _locationListener;
    private String _bestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_menu);
        btnAddParty = (ImageButton) findViewById(R.id.btnAddParty);


        //Add MapFragment (in code)
//        if (savedInstanceState == null)
//        {
//            _mapFragment = MapFragment.newInstance();
//            getFragmentManager().beginTransaction().add(R.id.container, _mapFragment, "map").commit();
//        }


        //fragment ophalen
        _mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        //set the callback on the fragment
        _mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        // Acquire a reference to the system Location Manager
        _locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean isProviderEnabled = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isProviderEnabled)
        {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        Criteria criteria = new Criteria();
        _bestProvider = _locationManager.getBestProvider(criteria, false);

        _locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                MarkerOptions options = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are here");
                googleMap.addMarker(options);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider)
            {
                Toast.makeText((Context) _locationListener, "Enabled new provider " + _bestProvider, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Toast.makeText((Context) _locationListener, "Disabled provider " + _bestProvider, Toast.LENGTH_SHORT).show();
            }
        };


        // Register the listener with the Location Manager to receive location updates
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Location lastKnownLocation = _locationManager.getLastKnownLocation(_bestProvider);
        if (lastKnownLocation != null) _locationListener.onLocationChanged(lastKnownLocation);
    }



    @Override
    protected void onResume()
    {
        super.onResume();
        if (_locationManager != null) _locationManager.requestLocationUpdates(_bestProvider, 0, 0, _locationListener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        _locationManager.removeUpdates(_locationListener);
    }
}