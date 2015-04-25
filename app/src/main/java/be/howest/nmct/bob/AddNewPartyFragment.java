package be.howest.nmct.bob;


import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class AddNewPartyFragment extends Fragment
{
    private ImageView imgPartyPicture;
    private Uri _picture;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CROP = 2;


    //constructor
    public AddNewPartyFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_new_party, container, false);

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
        if (takePicutreIntent.resolveActivity(getActivity().getPackageManager()) != null) startActivityForResult(takePicutreIntent, REQUEST_IMAGE_CAPTURE);
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
                Bitmap imageBitmap = (Bitmap) extras.getParcelable("data");
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

    public static AddNewPartyFragment newInstance(Location location)
    {
        AddNewPartyFragment anpFragment = new AddNewPartyFragment();

        return anpFragment;
    }


}
