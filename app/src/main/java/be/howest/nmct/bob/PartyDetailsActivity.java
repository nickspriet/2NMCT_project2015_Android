package be.howest.nmct.bob;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import be.howest.nmct.bob.admin.Party;
import be.howest.nmct.bob.admin.PartyAdmin;


public class PartyDetailsActivity extends FragmentActivity
{
    public static final String EXTRA_DETAIL_ID = "be.howest.nmct.bob.DETAIL_ID";
    public static final String EXTRA_DETAIL_NAME = "be.howest.nmct.bob.DETAIL_NAME";
    private PartyDetailsFragmentAdapter _pdfAdpater;
    private ViewPager _viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);

        // Create the adapter that will return a fragment for each of the sections of the app.
        _pdfAdpater = new PartyDetailsFragmentAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        _viewPager = (ViewPager) findViewById(R.id.pager);
        _viewPager.setAdapter(_pdfAdpater);
    }

    private class PartyDetailsFragmentAdapter extends FragmentStatePagerAdapter
    {
        public PartyDetailsFragmentAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Party p = PartyAdmin.getParties().get(position);

            Fragment partydetailsfrag = PartyDetailsFragment.newInstance(p);
            return partydetailsfrag;
        }

        @Override
        public int getCount()
        {
            return PartyAdmin.getParties().size();
        }
    }
}
