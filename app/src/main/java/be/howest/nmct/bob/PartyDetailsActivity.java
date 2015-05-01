package be.howest.nmct.bob;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class PartyDetailsActivity extends ActionBarActivity
{
    private PartyDetailsFragmentAdapter _pdfAdpater;
    private ViewPager _viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);

        // Create the adapter that will return a fragment for each of the sections of the app.
        _pdfAdpater = new PartyDetailsFragmentAdapter(getSupportFragmentManager());

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
            return null;
        }

        @Override
        public int getCount()
        {
            return 0;
        }
    }
}
