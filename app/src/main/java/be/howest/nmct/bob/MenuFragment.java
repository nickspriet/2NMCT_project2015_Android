package be.howest.nmct.bob;


import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends ListFragment
{
    private MenuItemsAdapter _miAdapter;


    //constructor
    public MenuFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _miAdapter = new MenuItemsAdapter();
        setListAdapter(_miAdapter);
    }

    public enum MENUITEM
    {
        SHOWPARTIES("Show parties"),
        SHOWMAP("Show map");

        String title;

        //constructor
        MENUITEM(String title)
        {
            this.title = title;
        }

        public String getTitle()
        {
            return title;
        }
    }

    public class MenuItemsAdapter extends ArrayAdapter<MENUITEM>
    {
        //constructor
        public MenuItemsAdapter()
        {
            super(getActivity(), R.layout.row_menu, R.id.tvPartyName, MENUITEM.values());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View rowMenuItem = super.getView(position, convertView, parent);

            MENUITEM item = MENUITEM.values()[position];

            TextView tvPartyName = (TextView) rowMenuItem.findViewById(R.id.tvPartyName);
            tvPartyName.setText(item.getTitle());

            return rowMenuItem;
        }
    }
}
