package be.howest.nmct.bob;


import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private EditText etAddress;
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

    private double _lat;
    private double _long;
    private Bitmap _imgBitmap;
    private String mCurrentPhotoPath;
    private OnPartySaveListener _opsListener;


    //constructor
    public AddNewPartyFragment()
    {
        // Required empty public constructor
    }

    public AddNewPartyFragment(Marker myLocationMarker)
    {
        this._myMarker = myLocationMarker;
        _lat = _myMarker.getPosition().latitude;
        _long = _myMarker.getPosition().longitude;
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
                //save party in database
                if (isFormComplete()) saveParty();

                //upload image to server
                new UploadPictureTask().execute(_imgBitmap);

                _opsListener.onPartySave(new LatLng(_lat, _long));
            }
        });

        return v;
    }

    private boolean isFormComplete()
    {
        if (imgPartyPicture.getDrawable() != null &&
                !etPartyName.getText().toString().isEmpty() &&
                !etDescription.getText().toString().isEmpty() &&
                !etAddress.getText().toString().isEmpty() &&
                !etZipcode.getText().toString().isEmpty() &&
                !etCity.getText().toString().isEmpty() &&
                !etFrom.getText().toString().isEmpty() &&
                !etUntil.getText().toString().isEmpty())
        {
            //everything is filled in correctly !
            return true;
        }
        else
        {
            //highlight all the required fields that are not filled in !
            if (imgPartyPicture.getDrawable() == null) highlightIncompleteFields(imgPartyPicture);
            if (etPartyName.getText().toString().isEmpty()) highlightIncompleteFields(etPartyName);
            if (etDescription.getText().toString().isEmpty()) highlightIncompleteFields(etDescription);
            if (etAddress.getText().toString().isEmpty()) highlightIncompleteFields(etAddress);
            if (etZipcode.getText().toString().isEmpty()) highlightIncompleteFields(etZipcode);
            if (etCity.getText().toString().isEmpty()) highlightIncompleteFields(etCity);
            if (etFrom.getText().toString().isEmpty()) highlightIncompleteFields(etFrom);
            if (etUntil.getText().toString().isEmpty()) highlightIncompleteFields(etUntil);

            return false;
        }
    }

    private void highlightIncompleteFields(View field)
    {
        if (field instanceof ImageView)
        {
            ImageView imgv = (ImageView) field;

        }
        else
        {
            EditText et = (EditText) field;
            et.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        }
    }

    private void saveParty()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                Party newParty = new Party();
                newParty.setID(PartyAdmin.getParties().size());
                newParty.setName(etPartyName.getText().toString());
                newParty.setDescription(etDescription.getText().toString());

                Bitmap bmPicture = ((BitmapDrawable) imgPartyPicture.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmPicture.compress(Bitmap.CompressFormat.PNG, 0, stream);
                newParty.setPicture(stream.toByteArray());

                newParty.setAddress(etAddress.getText().toString());
                newParty.setZipcode(etZipcode.getText().toString());
                newParty.setCity(etCity.getText().toString());

                //SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                newParty.setFromDate(etFrom.getText().toString());
                newParty.setUntilDate(etUntil.getText().toString());


                if (!etPresale.getText().toString().isEmpty()) newParty.setPricePresale(Double.parseDouble(etPresale.getText().toString()));
                else newParty.setPricePresale(0.0);
                if (!etAtTheDoor.getText().toString().isEmpty()) newParty.setPriceAtTheDoor(Double.parseDouble(etAtTheDoor.getText().toString()));
                else newParty.setPriceAtTheDoor(0.0);

                newParty.setDiskJockey1(etDiskJockey1.getText().toString());
                newParty.setDiskJockey2(etDiskJockey2.getText().toString());
                newParty.setDiskJockey3(etDiskJockey3.getText().toString());

                newParty.setLatitude(_lat);
                newParty.setLongitude(_long);

                //post party to server
                new SubmitPartyTask().execute(newParty);

            }
        }).start();
    }

    private void fillAddressWithDataFromMarker(View v)
    {
        //fill in address with data from marker
        if (_addresses != null)
        {
            etAddress = (EditText) v.findViewById(R.id.etAddress);
            etAddress.setText(_addresses.get(0).getAddressLine(0));

            etZipcode = (EditText) v.findViewById(R.id.etZipcode);
            etZipcode.setText(_addresses.get(0).getPostalCode());

            etCity = (EditText) v.findViewById(R.id.etCity);
            etCity.setText(_addresses.get(0).getLocality());
        }
    }


    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            File photoFile = null;

            try
            {
                photoFile = createImageFile();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (photoFile != null)
            {
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    //http://developer.android.com/training/camera/photobasics.html
    private File createImageFile() throws IOException
    {
        // Create an image file name
        String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
        File dir = new File(storageDir);
        if (!dir.exists()) dir.mkdir();

        File image = new File(storageDir + "/", PartyAdmin.getParties().size() + ".jpg");
        if (image.exists())
        {
            image.delete();
            image = new File(storageDir + "/", PartyAdmin.getParties().size() + ".jpg");
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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

                _imgBitmap = (Bitmap) data.getExtras().get("data");
                imgPartyPicture.setImageBitmap(_imgBitmap);
            }
        }
    }


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
            calendarFrom.set(Calendar.MINUTE, minute);
            updateFrom();
        }
        else if (calendarUntil != null)
        {
            calendarUntil.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarUntil.set(Calendar.MINUTE, minute);
            updateUntil();
        }
        else return;
    }

    private void updateFrom()
    {
        int yearFrom = calendarFrom.get(Calendar.YEAR) - 1900;  //zie Date-klasse (Gregoriaanse tijd start vanaf 1900)
        int monthFrom = calendarFrom.get(Calendar.MONTH);
        int dayFrom = calendarFrom.get(Calendar.DAY_OF_MONTH);
        int hourFrom = calendarFrom.get(Calendar.HOUR_OF_DAY);
        int minuteFrom = calendarFrom.get(Calendar.MINUTE);
        Date dateFrom = new Date(yearFrom, monthFrom, dayFrom, hourFrom, minuteFrom);

        //etFrom.setText(dateFormat.format(calendarFrom.getTime()) + " " + timeFormat.format(calendarFrom.getTime()));
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        etFrom.setText(dateformat.format(dateFrom));
        calendarFrom = null;
    }

    private void updateUntil()
    {
        int yearUntil = calendarUntil.get(Calendar.YEAR) - 1900;    //zie Date-klasse (Gregoriaanse tijd start vanaf 1900)
        int monthUntil = calendarUntil.get(Calendar.MONTH);
        int dayUntil = calendarUntil.get(Calendar.DAY_OF_MONTH);
        int hourUntil = calendarUntil.get(Calendar.HOUR_OF_DAY);
        int minuteUntil = calendarUntil.get(Calendar.MINUTE);
        Date dateUntil = new Date(yearUntil, monthUntil, dayUntil, hourUntil, minuteUntil);

        //etUntil.setText(dateFormat.format(calendarUntil.getTime()) + " " + timeFormat.format(calendarUntil.getTime()));
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        etUntil.setText(dateformat.format(dateUntil));
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

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            if (aBoolean) Toast.makeText(getActivity().getApplicationContext(), "New party has been created", Toast.LENGTH_SHORT).show();
        }
    }

    private String writeParty(Party party)
    {
        String result = NetworkUtils.post("https://student.howest.be/nick.spriet/BOB/parties3.php", party);
        return result;
    }

    private class UploadPictureTask extends AsyncTask<Bitmap, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Bitmap... params)
        {
            try
            {
                if (uploadImage(params[0]) != null) return true;
                else return false;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            if (aBoolean) Toast.makeText(getActivity().getApplicationContext(), "Image uploaded sucessfully", Toast.LENGTH_SHORT).show();
        }
    }

    private String uploadImage(Bitmap param) throws IOException
    {
        String result = NetworkUtils.uploadImage("https://student.howest.be/nick.spriet/BOB/uploadimage.php", _imgBitmap, mCurrentPhotoPath);
        return result;
    }



    public interface OnPartySaveListener
    {
        public void onPartySave(LatLng pos);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try { _opsListener = (OnPartySaveListener) activity; }
        catch (ClassCastException ccEx) { throw new ClassCastException(activity.toString() + " must implement OnPartySaveListener"); }
    }
}

