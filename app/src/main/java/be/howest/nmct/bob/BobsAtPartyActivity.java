package be.howest.nmct.bob;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import be.howest.nmct.bob.admin.PartyAdmin;
import be.howest.nmct.bob.loader.PartyBobLoader;


public class BobsAtPartyActivity extends Activity
{
    public static final String EXTRA_PARTY_ID = "be.howest.nmct.bob.PARTY_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bobs_at_party);

        if (savedInstanceState == null)
        {
            int partyid = getIntent().getIntExtra(EXTRA_PARTY_ID, 0);

            showBobsAtPartyFragment(partyid);

        }
    }

    private void showBobsAtPartyFragment(int partyid)
    {
        Fragment bapFragment = new BobsAtPartyFragment(partyid);

        //getFragmentManager().popBackStack();

        getFragmentManager().beginTransaction()
                .replace(R.id.bobcontainer, bapFragment)
                .commit();

        Bundle args = new Bundle();
        args.putInt("VIEWPAGER_POSITION", partyid);
        onSaveInstanceState(args);

        setTitle("BOBs @ " + PartyAdmin.getPartyByID(partyid).getName());
    }
}
