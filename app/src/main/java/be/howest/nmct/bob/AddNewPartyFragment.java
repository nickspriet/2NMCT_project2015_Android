package be.howest.nmct.bob;


import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class AddNewPartyFragment extends Fragment
{
    private ImageView imgPartyPicture;
    private EditText etStreet;
    private EditText etZipcode;
    private EditText etCity;
    private Uri _picture;
    private Marker _myMarker;
    private List<Address> _addresses;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CROP = 2;


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

        etStreet = (EditText) v.findViewById(R.id.etStreet);
        etStreet.setText(_addresses.get(0).getAddressLine(0));

        etZipcode = (EditText) v.findViewById(R.id.etZipcode);
        etZipcode.setText(_addresses.get(0).getPostalCode());

        etCity = (EditText) v.findViewById(R.id.etCity);
        etCity.setText(_addresses.get(0).getLocality());


        imgPartyPicture = (ImageView) v.findViewById(R.id.imgPartyPicture);
        imgPartyPicture.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
            }
        });


        return v;
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePicutreIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicutreIntent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivityForResult(takePicutreIntent, REQUEST_IMAGE_CAPTURE);
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
                CropImage();
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


    private void CropImage()
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(_picture, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 415);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_IMAGE_CROP);
    }


    public AddNewPartyFragment newInstance(Marker myLocationMarker)
    {
        AddNewPartyFragment anpFragment = new AddNewPartyFragment();
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

        return anpFragment;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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
}
