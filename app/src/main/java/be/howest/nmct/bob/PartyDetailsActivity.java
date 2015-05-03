package be.howest.nmct.bob;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import be.howest.nmct.bob.admin.Party;
import be.howest.nmct.bob.admin.PartyAdmin;


public class PartyDetailsActivity extends FragmentActivity
    implements PartyDetailsFragment.OnBobAtPartyListener
{
    public static String EXTRA_POSITION = "be.howest.nmct.bob.EXTRA_POSITION";
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

        //set start position of ViewPager on the position of clicked item
        _viewPager.setCurrentItem(getIntent().getIntExtra(EXTRA_POSITION, 0));

        //change title of actionbar to Party Name
        getActionBar().setTitle(PartyAdmin.getPartyByID(getIntent().getIntExtra(EXTRA_POSITION, 0)).getName());

        _viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                //change title of actionbar to Party Name
                getActionBar().setTitle(PartyAdmin.getPartyByID(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });


        changeStatusBarColor();
    }

    private void changeStatusBarColor()
    {
        //change color of statusbar
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(new ColorDrawable(this.getResources().getColor(R.color.bgStatusBar)).getColor());

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


    @Override
    public void onBobAtParty(int partyID)
    {
        //go to BobsAtPartyActivity and pass the partyID
        Intent intent = new Intent(this, BobsAtPartyActivity.class);
        intent.putExtra(BobsAtPartyActivity.EXTRA_PARTY_ID, partyID);

        startActivity(intent);
        finish();
    }
}
