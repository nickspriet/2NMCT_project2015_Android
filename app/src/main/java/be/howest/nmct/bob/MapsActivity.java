package be.howest.nmct.bob;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.melnykov.fab.FloatingActionButton;

import be.howest.nmct.bob.admin.Party;
import be.howest.nmct.bob.admin.PartyAdmin;
import be.howest.nmct.bob.helper.BitmapToMutable;
import be.howest.nmct.bob.loader.Contract;
import be.howest.nmct.bob.loader.PartyLoader;


public class MapsActivity extends FragmentActivity
        implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OnMapReadyCallback,
        MenuFragment.OnMenuItemSelectedListener,
        PartiesFragment.OnPartySelectedListener,
        AddNewPartyFragment.OnPartySaveListener
{
    private DrawerLayout _drawerLayout;
    private FloatingActionButton btnAddParty;
    private TextView tvLatitude;
    private TextView tvLongitude;

    private PartiesFragment _partiesFragment;
    private MapFragment _mapFragment;
    private GoogleMap _map;
    private LocationManager _locationManager;
    private LocationListener _locationListener;
    private Location _lastKnownLocation;
    private String _bestProvider;
    private Marker _myLocationMarker;
    private int _zoomLevel = 10;
    private MenuFragment.MENUITEM _selectedMenuItem;


    public static final int REQUEST_DETAIL = 1;
    private Loader<Cursor> _loader;
    private boolean stopUpdatingGPS = false;
    private Marker _partyMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_menu);

        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
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
            //init fragments
            _mapFragment = MapFragment.newInstance();
            _partiesFragment = new PartiesFragment();

            showMapFragment();
        }

        changeStatusBarColor();
        getLoaderManager();
    }

    private void changeStatusBarColor()
    {
        //change color of statusbar
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(new ColorDrawable(this.getResources().getColor(R.color.bgStatusBar)).getColor());
    }

    private void showMapFragment()
    {
        Fragment mapfrag = getFragmentManager().findFragmentByTag("mapfrag");
        if (mapfrag == null || !mapfrag.isVisible())
        {
            //getFragmentManager().popBackStack();

            getFragmentManager().beginTransaction()
                    .replace(R.id.mapcontainer, _mapFragment, "mapfrag")
                    //.addToBackStack("ShowMapFragment")
                    .commit();

            //set the callback on the fragment
            _mapFragment.getMapAsync(this);

            fillAdapterWithPartyData();


            setTitle("BOB");
            btnAddParty.setVisibility(View.VISIBLE);
        }

        _drawerLayout.closeDrawer(Gravity.LEFT);
    }


    @Override
    public void onBackPressed()
    {
        //zet floating action button terug op visible
        View v = findViewById(R.id.container);
        if (v != null && btnAddParty.getVisibility() == View.INVISIBLE) btnAddParty.setVisibility(View.VISIBLE);

        if (getFragmentManager().getBackStackEntryCount() != 0) getFragmentManager().popBackStack();
        else super.onBackPressed();
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        _partiesFragment = new PartiesFragment();
        _mapFragment = MapFragment.newInstance();
        Fragment mapfrag = getFragmentManager().findFragmentById(R.id.mapcontainer);
        if (mapfrag != null && mapfrag instanceof MapFragment) ((MapFragment)mapfrag).getMapAsync(this);
        fillAdapterWithPartyData();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();

        _partiesFragment = new PartiesFragment();
        _mapFragment = MapFragment.newInstance();
        Fragment mapfrag = getFragmentManager().findFragmentById(R.id.mapcontainer);
        if (mapfrag != null && mapfrag instanceof MapFragment) ((MapFragment)mapfrag).getMapAsync(this);
        fillAdapterWithPartyData();
    }

    private void fillAdapterWithPartyData()
    {
        String[] columns = new String[]
                {
                        Contract.PartyColumns.COLUMN_PARTY_ID,
                        Contract.PartyColumns.COLUMN_PARTY_NAME,
                        Contract.PartyColumns.COLUMN_PARTY_PICTURE
                };

        int[] viewIds = new int[]
                {
                        R.id.tvName,
                        R.id.imgPicture
                };

        //initialisatie adapter
        _partiesFragment._pAdapter = new PartiesFragment.PartyAdapter(MapsActivity.this.getApplicationContext(), R.layout.row_party, null, columns, viewIds, 0);

        //activeer de loader
        getLoaderManager().restartLoader(0, null, MapsActivity.this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        _map = googleMap;

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


        initOnMarkerDragListener(googleMap);
    }


    private void addPartyMarkers(final GoogleMap googleMap)
    {
        //Place marker for each party
        for (Party party : PartyAdmin.getParties())
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(party.getPicture(), 0, party.getPicture().length);
            bitmap = BitmapToMutable.convertToMutable(bitmap);

            //crop & scale bitmap
            if (bitmap.getWidth() >= bitmap.getHeight())
            {
                bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth()/2 - bitmap.getHeight()/2, 0, bitmap.getHeight(), bitmap.getHeight());
                bitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, false);
            }
            else
            {
                bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()/2 - bitmap.getWidth()/2, bitmap.getWidth(), bitmap.getWidth());
                bitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, false);
            }


            MarkerOptions partyOptions = new MarkerOptions()
                    .position(new LatLng(party.getLatitude(), party.getLongitude()))
                    .title(party.getName())
                    .snippet(party.getCity())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

            _partyMarker = googleMap.addMarker(partyOptions);
        }
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

                if (_lastKnownLocation == null || _myLocationMarker == null) return;

                if (!_lastKnownLocation.equals(location) && !stopUpdatingGPS)
                {
                    _myLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    _lastKnownLocation = location;
                }
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
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(48.0f));

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


    private void initOnMarkerDragListener(GoogleMap googleMap)
    {
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
        {

            @Override
            public void onMarkerDragStart(Marker marker)
            {
                //stop de LocationListener met de locatie op te halen
                stopUpdatingGPS = true;
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

    @Override
    public void onMenuItemSelected(MenuFragment.MENUITEM item)
    {
        _selectedMenuItem = item;

        if (_selectedMenuItem == MenuFragment.MENUITEM.PARTIES) showPartiesFragment();
        else showMapFragment();
    }

    @Override
    public void onPartySelected(int partyid)
    {
        //get party by partyid
        Party party = PartyAdmin.getPartyByID(partyid);

        //go to PartyDetailsActivity
        Intent intent = new Intent(this, PartyDetailsActivity.class);
        intent.putExtra(PartyDetailsActivity.EXTRA_POSITION, party.getID());

        startActivity(intent);
    }

    private void showAddNewPartyFragment(View v)
    {
        if (_myLocationMarker == null) return;

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

    private void showPartiesFragment()
    {
        Fragment partiesfrag = getFragmentManager().findFragmentByTag("partiesfrag");

        if (partiesfrag == null || !partiesfrag.isVisible())
        {
            getFragmentManager().beginTransaction()
                    .replace(R.id.mapcontainer, _partiesFragment, "partiesfrag")
                    .addToBackStack("ShowPartiesFragment")
                    .commit();

            setTitle("Parties nearby");

            btnAddParty.setVisibility(View.INVISIBLE);

            if (_locationManager != null) _locationManager.removeUpdates(_locationListener);
        }

        _drawerLayout.closeDrawer(Gravity.LEFT);
    }


    //region **METHODS FOR LOADER**
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new PartyLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        _loader = loader;
        _partiesFragment._pAdapter.swapCursor(cursor);
        if (_map != null) addPartyMarkers(_map);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        _partiesFragment._pAdapter.swapCursor(null);
    }

    @Override
    public void onPartySave(LatLng pos)
    {
        showMapFragment();
        _map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pos.latitude, pos.longitude), _zoomLevel));
        _partyMarker.remove();
        fillAdapterWithPartyData();
    }
    //endregion

}