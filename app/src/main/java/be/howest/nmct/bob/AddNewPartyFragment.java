package be.howest.nmct.bob;


import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import be.howest.nmct.bob.admin.Party;
import be.howest.nmct.bob.admin.PartyAdmin;
import be.howest.nmct.bob.helper.NetworkUtils;

public class AddNewPartyFragment extends Fragment
    implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener
{
    private ImageView imgPartyPicture;
    private EditText etPartyName;
    private EditText etDescription;
    private EditText etStreet;
    private EditText etZipcode;
    private EditText etCity;
    private EditText etFrom;
    private EditText etUntil;
    private EditText etPresale;
    private EditText etAtTheDoor;
    private EditText etDiskJockey1;
    private EditText etDiskJockey2;
    private EditText etDiskJockey3;
    private Button btnSaveParty;
    private Uri _picture;
    private Marker _myMarker;
    private List<Address> _addresses;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CROP = 2;


    private static final String TIME_PATTERN = "HH:mm";
    private Calendar calendarFrom;
    private Calendar calendarUntil;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;


    //constructor
    public AddNewPartyFragment()
    {
        // Required empty public constructor
    }

    public AddNewPartyFragment(Marker myLocationMarker)
    {
        this._myMarker = myLocationMarker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_new_party, container, false);

        fillAddressWithDataFromMarker(v);
        initDateTimePicker(v);

        etPartyName = (EditText) v.findViewById(R.id.etPartyName);
        etPartyName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0)
                {
                    etPartyName.getText().toString().toUpperCase();
                    etPartyName.setTypeface(null, Typeface.BOLD);
                }
                else
                {
                    etPartyName.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        imgPartyPicture = (ImageView) v.findViewById(R.id.imgPartyPicture);
        imgPartyPicture.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
            }
        });

        etDescription = (EditText) v.findViewById(R.id.etDescription);
        etPresale = (EditText) v.findViewById(R.id.etPresale);
        etAtTheDoor = (EditText) v.findViewById(R.id.etAtTheDoor);
        etDiskJockey1 = (EditText) v.findViewById(R.id.etDiskJockey1);
        etDiskJockey2 = (EditText) v.findViewById(R.id.etDiskJockey2);
        etDiskJockey3 = (EditText) v.findViewById(R.id.etDiskJockey3);

        btnSaveParty = (Button) v.findViewById(R.id.btnSaveParty);
        btnSaveParty.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveParty();
            }
        });

        return v;
    }

    private void saveParty()
    {
        Party newParty = new Party();
        newParty.setID(PartyAdmin.getParties().size());
        newParty.setName(etPartyName.getText().toString());
        newParty.setDescription(etDescription.getText().toString());
        //newParty.setPicture();
        newParty.setAddress(etStreet.getText().toString());
        newParty.setZipcode(etZipcode.getText().toString());
        newParty.setCity(etCity.getText().toString());
        //newParty.setFromDate(etFrom.getText().toString());
        //newParty.setUntilDate(etUntil.getText().toString());
        newParty.setPricePresale(Double.parseDouble(etPresale.getText().toString()));
        newParty.setPriceAtTheDoor(Double.parseDouble(etAtTheDoor.getText().toString()));
        newParty.setDiskJockey1(etDiskJockey1.getText().toString());
        newParty.setDiskJockey2(etDiskJockey2.getText().toString());
        newParty.setDiskJockey3(etDiskJockey3.getText().toString());
        newParty.setLatitude(_myMarker.getPosition().latitude);
        newParty.setLongitude(_myMarker.getPosition().longitude);

        //post party to server
        new SubmitPartyTask().execute(newParty);
    }

    private void fillAddressWithDataFromMarker(View v)
    {
        //fill in address with data from marker
        if (_addresses != null)
        {
            etStreet = (EditText) v.findViewById(R.id.etStreet);
            etStreet.setText(_addresses.get(0).getAddressLine(0));

            etZipcode    = (EditText) v.findViewById(R.id.etZipcode);
            etZipcode.setText(_addresses.get(0).getPostalCode());

            etCity = (EditText) v.findViewById(R.id.etCity);
            etCity.setText(_addresses.get(0).getLocality());
        }
    }


    private void dispatchTakePictureIntent()
    {
        Intent takePicutreIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicutreIntent.resolveActivity(getActivity().getPackageManager()) != null) startActivityForResult(takePicutreIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //get information of Marker-location
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

        try
        {
            _addresses = geocoder.getFromLocation(_myMarker.getPosition().latitude, _myMarker.getPosition().longitude, 1);
        }
        catch (IOException ioEx)
        {
            Log.d("ioex", ioEx.getMessage());
        }
    }

    private void initDateTimePicker(View v)
    {
        //DatePicker & TimePicker initialisation
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        etFrom = (EditText) v.findViewById(R.id.etFrom);
        etFrom.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus) return;

                calendarFrom = Calendar.getInstance();
                DatePickerDialog.newInstance(AddNewPartyFragment.this, calendarFrom.get(Calendar.YEAR), calendarFrom.get(Calendar.MONTH), calendarFrom.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");

            }
        });

        etUntil = (EditText) v.findViewById(R.id.etUntil);
        etUntil.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus) return;

                calendarUntil = Calendar.getInstance();
                DatePickerDialog.newInstance(AddNewPartyFragment.this, calendarUntil.get(Calendar.YEAR), calendarUntil.get(Calendar.MONTH), calendarUntil.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == getActivity().RESULT_OK)
        {
            if (requestCode == REQUEST_IMAGE_CAPTURE)
            {
                //get the Uri for the caputured image
                _picture = data.getData();

                //crop image
                //CropImage();

                imgPartyPicture.setImageBitmap((Bitmap) data.getExtras().get("data"));

            }
            else if (requestCode == REQUEST_IMAGE_CROP)
            {
                //get cropped image
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = extras.getParcelable("data");
                imgPartyPicture.setImageBitmap(imageBitmap);
            }
        }
    }


//    private void CropImage()
//    {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(_picture, "image/*");
//        intent.putExtra("crop", true);
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 415);
//        intent.putExtra("outputY", 200);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, REQUEST_IMAGE_CROP);
//    }


//    public AddNewPartyFragment newInstance(Marker myLocationMarker)
//    {
//        AddNewPartyFragment anpFragment = new AddNewPartyFragment();
//
//        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
//
//        try
//        {
//            List<Address> addresses = geocoder.getFromLocation(myLocationMarker.getPosition().latitude, myLocationMarker.getPosition().longitude, 1);
//            etStreet.setText(addresses.get(0).getAddressLine(0));
//            etZipcode.setText(addresses.get(0).getPostalCode());
//            etCity.setText(addresses.get(0).getLocality());
//        }
//        catch (IOException ioEx)
//        {
//            Log.d("ioex", ioEx.getMessage());
//        }
//
//        return anpFragment;
//    }


    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }


    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth)
    {
        if (calendarFrom != null)
        {
            //open timepicker after datepicker
            calendarFrom.set(year, monthOfYear, dayOfMonth);
            TimePickerDialog.newInstance(AddNewPartyFragment.this, calendarFrom.get(Calendar.HOUR_OF_DAY), calendarFrom.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
        }
        else if (calendarUntil != null)
        {
            //open timepicker after datepicker
            calendarUntil.set(year, monthOfYear, dayOfMonth);
            TimePickerDialog.newInstance(AddNewPartyFragment.this, calendarUntil.get(Calendar.HOUR_OF_DAY), calendarUntil.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
        }
        else return;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
    {
        if (calendarFrom != null)
        {
            calendarFrom.set(Calendar.HOUR_OF_DAY, hourOfDay);
            updateFrom();
        }
        else if (calendarUntil != null)
        {
            calendarUntil.set(Calendar.MINUTE, minute);
            updateUntil();
        }
        else return;
    }

    private void updateFrom()
    {
        etFrom.setText(dateFormat.format(calendarFrom.getTime()) + " " + timeFormat.format(calendarFrom.getTime()));
        calendarFrom = null;
    }

    private void updateUntil()
    {
        etUntil.setText(dateFormat.format(calendarUntil.getTime()) + " " + timeFormat.format(calendarUntil.getTime()));
        calendarUntil = null;
    }



    public class SubmitPartyTask extends AsyncTask<Party, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(Party... params)
        {
            if (writeParty(params[0]) != null) return true;
            else return false;
        }
    }

    private String writeParty(Party party)
    {
        return null;
    }
}

