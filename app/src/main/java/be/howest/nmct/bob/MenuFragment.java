package be.howest.nmct.bob;


import android.animation.Animator;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MenuFragment extends ListFragment
{
    private TextView tvMenuTitle;
    private ImageView imgMenuIcon;
    private ListView lvMenuItems;

    private MenuItemsAdapter _miAdapter;
    private OnMenuItemSelectedListener _omisListener;

    //constructor
    public MenuFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_menu, container, false);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _miAdapter = new MenuItemsAdapter();
        setListAdapter(_miAdapter);

        //markeerPositieInListview(0);
    }


    public enum MENUITEM
    {
        MAP("Show map"),
        PARTIES("Show parties");

        String title;

        //constructor
        MENUITEM(String title)
        {
            this.title = title;
        }

        //getters
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
            super(getActivity(), R.layout.row_menu, R.id.tvMenuTitle, MENUITEM.values());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View rowMenuItem = super.getView(position, convertView, parent);

            MENUITEM item = MENUITEM.values()[position];

            tvMenuTitle = (TextView) rowMenuItem.findViewById(R.id.tvMenuTitle);
            tvMenuTitle.setText(item.getTitle());

            imgMenuIcon = (ImageView) rowMenuItem.findViewById(R.id.imgMenuIcon);
            imgMenuIcon.setImageResource(getResourceID(item));

            //highlight first item in list
            if (position == 0)
            {
                rowMenuItem.findViewById(R.id.layoutMenuItem).setBackgroundColor(Color.LTGRAY);
                tvMenuTitle.setTextColor(Color.WHITE);
            }

            return rowMenuItem;
        }
    }

    private int getResourceID(MENUITEM item)
    {
        switch (item)
        {
            case MAP:
                return R.drawable.ic_world;
            default:
                return R.drawable.ic_partyhead;
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        //highlight active ListViewItem
        highlightActiveMenuItem(position);

        MENUITEM item = _miAdapter.getItem(position);
        _omisListener.onMenuItemSelected(item);
    }

    public void highlightActiveMenuItem(int pos)
    {
        //style terugzetten van alle items in list
        for (int i = 0; i < getListView().getChildCount(); i++)
        {
            getListView().getChildAt(i).findViewById(R.id.layoutMenuItem).setBackgroundColor(Color.TRANSPARENT);
            TextView tvMenuTitle = (TextView) getListView().getChildAt(i).findViewById(R.id.tvMenuTitle);
            tvMenuTitle.setTextColor(Color.GRAY);
        }

        //startRippleAnimation(getListView().getChildAt(pos));

        //highlight active menu-item
        getListView().getChildAt(pos).findViewById(R.id.layoutMenuItem).setBackgroundColor(Color.LTGRAY);
        ((TextView) (getListView().getChildAt(pos).findViewById(R.id.tvMenuTitle))).setTextColor(Color.WHITE);
    }


    private void startRippleAnimation(View v)
    {
        // previously invisible view
        View viewMenuItem = v.findViewById(R.id.layoutMenuItem);

        // get the center for the clipping circle
        int cx = (viewMenuItem.getLeft() + viewMenuItem.getRight()) / 2;
        int cy = (viewMenuItem.getTop() + viewMenuItem.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(viewMenuItem.getWidth(), viewMenuItem.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(viewMenuItem, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        viewMenuItem.setVisibility(View.VISIBLE);
        anim.start();
    }


    public interface OnMenuItemSelectedListener
    {
        public void onMenuItemSelected(MENUITEM item);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try { _omisListener = (OnMenuItemSelectedListener) activity; }
        catch (ClassCastException ccEx) { throw new ClassCastException(activity.toString() + " must implement OnMenuItemSelectedListener"); }

    }
}
