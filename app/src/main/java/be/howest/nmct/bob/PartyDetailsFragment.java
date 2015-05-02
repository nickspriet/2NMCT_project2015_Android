package be.howest.nmct.bob;


import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import be.howest.nmct.bob.admin.Party;


public class PartyDetailsFragment extends Fragment
{
    private ImageView imgDetailPicture;
    private TextView tvDetailDescription;
    private TextView tvDetailAddress;
    private TextView tvDetailZipcodeCity;
    private TextView tvDetailFromDate;
    private TextView tvDetailUntilDate;
    private TextView tvDetailPricePresale;
    private TextView tvDetailPriceAtTheDoor;
    private TextView tvDetailDiskJockey1;
    private TextView tvDetailDiskJockey2;
    private TextView tvDetailDiskJockey3;


    //arguments for new PartyDetailsFragment
    public static final String ARG_DETAIL_ID = "detail_id";
    public static final String ARG_DETAIL_NAME = "detail_name";
    public static final String ARG_DETAIL_DESCRIPTION = "detail_description";
    public static final String ARG_DETAIL_PICTURE = "detail_picture";
    public static final String ARG_DETAIL_ADDRESS = "detail_address";
    public static final String ARG_DETAIL_ZIPCODE = "detail_zipcode";
    public static final String ARG_DETAIL_CITY = "detail_city";
    public static final String ARG_DETAIL_FROMDATE = "detail_fromdate";
    public static final String ARG_DETAIL_UNTILDATE = "detail_untildate";
    public static final String ARG_DETAIL_PRICEPRESALE = "detail_pricepresale";
    public static final String ARG_DETAIL_PRICEATTHEDOOR = "detail_priceatthedoor";
    public static final String ARG_DETAIL_DISKJOCKEY1 = "detail_diskjockey1";
    public static final String ARG_DETAIL_DISKJOCKEY2 = "detail_diskjockey2";
    public static final String ARG_DETAIL_DISKJOCKEY3 = "detail_diskjockey3";
    public static final String ARG_DETAIL_LATITUDE = "detail_latitude";
    public static final String ARG_DETAIL_LONGITUDE = "detail_longitude";

    //constructor
    public PartyDetailsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_party_details, container, false);

        imgDetailPicture = (ImageView) v.findViewById(R.id.imgDetailPicture);
        byte[] bitmapdata = getArguments().getByteArray(ARG_DETAIL_PICTURE);
        imgDetailPicture.setImageBitmap(BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length));

        tvDetailDescription = (TextView) v.findViewById(R.id.tvDetailDescription);
        tvDetailDescription.setText(getArguments().getString(ARG_DETAIL_DESCRIPTION));

        tvDetailAddress = (TextView) v.findViewById(R.id.tvDetailAddress);
        tvDetailAddress.setText(getArguments().getString(ARG_DETAIL_ADDRESS));

        tvDetailZipcodeCity = (TextView) v.findViewById(R.id.tvDetailZipcodeCity);
        tvDetailZipcodeCity.setText(getArguments().getString(ARG_DETAIL_ZIPCODE) + ", " + getArguments().getString(ARG_DETAIL_CITY));

        tvDetailFromDate = (TextView) v.findViewById(R.id.tvDetailFromDate);
        tvDetailFromDate.setText(getArguments().getString(ARG_DETAIL_FROMDATE));

        tvDetailUntilDate = (TextView) v.findViewById(R.id.tvDetailUntilDate);
        tvDetailUntilDate.setText(getArguments().getString(ARG_DETAIL_UNTILDATE));

        tvDetailPricePresale = (TextView) v.findViewById(R.id.tvDetailPricePresale);
        tvDetailPricePresale.setText("Presale: € " + getArguments().getDouble(ARG_DETAIL_PRICEPRESALE));

        tvDetailPriceAtTheDoor = (TextView) v.findViewById(R.id.tvDetailPriceAtTheDoor);
        tvDetailPriceAtTheDoor.setText("At the door: € " + getArguments().getDouble(ARG_DETAIL_PRICEATTHEDOOR));

        tvDetailDiskJockey1 = (TextView) v.findViewById(R.id.tvDetailDiskJockey1);
        tvDetailDiskJockey1.setText(getArguments().getString(ARG_DETAIL_DISKJOCKEY1));

        tvDetailDiskJockey2 = (TextView) v.findViewById(R.id.tvDetailDiskJockey2);
        tvDetailDiskJockey2.setText(getArguments().getString(ARG_DETAIL_DISKJOCKEY2));

        tvDetailDiskJockey3 = (TextView) v.findViewById(R.id.tvDetailDiskJockey3);
        tvDetailDiskJockey3.setText(getArguments().getString(ARG_DETAIL_DISKJOCKEY3));


        return v;
    }


    public static Fragment newInstance(Party p)
    {
        PartyDetailsFragment pdfrag = new PartyDetailsFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_DETAIL_ID, p.getID());
        args.putString(ARG_DETAIL_NAME, p.getName());
        args.putString(ARG_DETAIL_DESCRIPTION, p.getDescription());
        args.putString(ARG_DETAIL_NAME, p.getName());
        args.putByteArray(ARG_DETAIL_PICTURE, p.getPicture());
        args.putString(ARG_DETAIL_ADDRESS, p.getAddress());
        args.putString(ARG_DETAIL_ZIPCODE, p.getZipcode());
        args.putString(ARG_DETAIL_CITY, p.getCity());
        args.putString(ARG_DETAIL_FROMDATE, p.getFromDate().toString());
        args.putString(ARG_DETAIL_UNTILDATE, p.getUntilDate().toString());
        args.putDouble(ARG_DETAIL_PRICEPRESALE, p.getPricePresale());
        args.putDouble(ARG_DETAIL_PRICEATTHEDOOR, p.getPriceAtTheDoor());
        args.putString(ARG_DETAIL_DISKJOCKEY1, p.getDiskJockey1());
        args.putString(ARG_DETAIL_DISKJOCKEY2, p.getDiskJockey2());
        args.putString(ARG_DETAIL_DISKJOCKEY3, p.getDiskJockey3());
        args.putDouble(ARG_DETAIL_LATITUDE, p.getLatitude());
        args.putDouble(ARG_DETAIL_LONGITUDE, p.getLongitude());

        pdfrag.setArguments(args);
        return pdfrag;
    }
   }
