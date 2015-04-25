package be.howest.nmct.bob;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class AddNewPartyFragment extends Fragment
{
    private ImageView imgPartyPicture;
    static final int REQUEST_IMAGE_CAPTURE = 1;

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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgPartyPicture.setImageBitmap(imageBitmap);
        }
    }

    public static AddNewPartyFragment newInstance(Location location)
    {
        AddNewPartyFragment anpFragment = new AddNewPartyFragment();

        return anpFragment;
    }


}
