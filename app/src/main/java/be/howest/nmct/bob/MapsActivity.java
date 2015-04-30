package be.howest.nmct.bob;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Visibility;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.melnykov.fab.FloatingActionButton;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback
{
    private DrawerLayout _drawerLayout;
    private FloatingActionButton btnAddParty;
    private TextView tvLatitude;
    private TextView tvLongitude;

    private MapFragment _mapFragment;
    private GoogleMap _map;
    private LocationManager _locationManager;
    private LocationListener _locationListener;
    private Location _lastKnownLocation;
    private String _bestProvider;
    private Marker _myLocationMarker;
    private int _zoomLevel = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_menu);
        btnAddParty = (FloatingActionButton) findViewById(R.id.btnAddParty);
        btnAddParty.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showAddNewPartyFragment(v);

                //hide button
                btnAddParty.setVisibility(v.INVISIBLE);
            }
        });


        //Add MapFragment (in code)
        if (savedInstanceState == null)
        {
            //nieuwe MapFragment aanmaken en toevoegen aan backstack
            _mapFragment = MapFragment.newInstance();
            getFragmentManager().beginTransaction().add(R.id.mapcontainer, _mapFragment, "mapfrag").commit();
        }

        //set the callback on the fragment
        _mapFragment.getMapAsync(this);


        //change color of statusbar
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(new ColorDrawable(this.getResources().getColor(R.color.bgStatusBar)).getColor());
    }

    @Override
    public void onBackPressed()
    {
        //zet floating action button terug op visible
        View v = findViewById(R.id.container);
        if (v != null && btnAddParty.getVisibility() == View.INVISIBLE)btnAddParty.setVisibility(View.VISIBLE);

        if (getFragmentManager().getBackStackEntryCount() != 0) getFragmentManager().popBackStack();
        else super.onBackPressed();
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        _map = _mapFragment.getMap();
        initOnMarkerDragListener();
    }

    private void showAddNewPartyFragment(View v)
    {
        //fragment ophalen
        AddNewPartyFragment anpFragment = new AddNewPartyFragment(_myLocationMarker);

        //verwijder alle fragments van de backstack
        //getFragmentManager().popBackStack();

        getFragmentManager().beginTransaction()
                .replace(R.id.mapcontainer, anpFragment)
                .addToBackStack("ShowAddNewPartyFragment")
                .commit();

        setTitle("Add new party");
    }


    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        // Acquire a reference to the system Location Manager
        _locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //setup the Location Listener
        initLocationListener(googleMap);

        //beste provider ophalen
        _bestProvider = _locationManager.getBestProvider(new Criteria(), false);
        if (_bestProvider != null)
        {
            Log.d("BEST PROVIDER", _bestProvider);
            _locationManager.requestLocationUpdates(_bestProvider, 0, 0, _locationListener);

            getLocationAndAddMarker(googleMap);
        }
        else
        {
            //Melding aan gebruiker geven dat locatie niet kan opgehaald worden
            Toast.makeText(this, "Go to settings to enable location service", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //startActivity(intent);
        }


        // Register the listener with the Location Manager to receive location updates
        //_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, _locationListener);
        //_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locationListener);
    }

    private void initLocationListener(final GoogleMap googleMap)
    {
        _locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                tvLatitude.setText("Latitude: " + location.getLatitude());
                tvLongitude.setText("Longitude: " + location.getLongitude());

                //if (_lastKnownLocation != location)
                //{
                //marker updaten (= verwijderen en opnieuw toevoegen)
                if (_myLocationMarker != null) _myLocationMarker.remove();

                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title("You are here")
                        .draggable(true);

                Log.d("NEW MARKER", options.getTitle());

                _myLocationMarker = googleMap.addMarker(options);

                _lastKnownLocation = location;


                //}
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider)
            {
                Toast.makeText(getApplicationContext(), "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Toast.makeText(getApplicationContext(), "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void getLocationAndAddMarker(final GoogleMap googleMap)
    {
        //locatie ophalen
        _lastKnownLocation = _locationManager.getLastKnownLocation(_bestProvider);
        if (_lastKnownLocation != null)
        {
        //marker updaten (= verwijderen en opnieuw toevoegen)
        if (_myLocationMarker != null) _myLocationMarker.remove();

        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(_lastKnownLocation.getLatitude(), _lastKnownLocation.getLongitude()))
                .title("You are here")
                .draggable(true);

        Log.d("NEW MARKER", options.getTitle());

        _myLocationMarker = googleMap.addMarker(options);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_lastKnownLocation.getLatitude(), _lastKnownLocation.getLongitude()), _zoomLevel));
    }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //if (_locationManager != null) _locationManager.requestLocationUpdates(_bestProvider, 0, 0, _locationListener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (_locationManager != null) _locationManager.removeUpdates(_locationListener);
    }


    private void initOnMarkerDragListener()
    {
        _map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
        {

            @Override
            public void onMarkerDragStart(Marker marker)
            {
                //stop de LocationListener met de locatie op te halen
                if (_locationManager != null) _locationManager.removeUpdates(_locationListener);
            }

            @Override
            public void onMarkerDrag(Marker marker)
            {
                _lastKnownLocation.setLatitude(marker.getPosition().latitude);
                _lastKnownLocation.setLongitude(marker.getPosition().longitude);

                tvLatitude.setText("Latitude: " + _lastKnownLocation.getLatitude());
                tvLongitude.setText("Longitude: " + _lastKnownLocation.getLongitude());
            }

            @Override
            public void onMarkerDragEnd(Marker marker)
            {
                Log.d("end drag", "DRAGGED");
            }
        });
    }
}