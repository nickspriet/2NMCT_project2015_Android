package be.howest.nmct.bob;


import android.animation.Animator;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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

    private MenuItemsAdapter _miAdapter;
    private View _selectedMenuItem;


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

        _selectedMenuItem = v.findViewById(R.id.layoutMenuItem);


        return v;
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
        MAP("Show map"),
        PARTIES("Show parties");

        String title;
        String imageName;

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


            //pas kleur aan van
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

        //reveal circular effect
        startRippleAnimation(v);

        if (_selectedMenuItem != null)
        {
            TextView vorig = (TextView) _selectedMenuItem.findViewById(R.id.tvMenuTitle);
            vorig.setTextColor(Color.GRAY);

            LinearLayout voriglayout = (LinearLayout) vorig.getParent();
            voriglayout.setBackgroundColor(Color.TRANSPARENT);
        }

        TextView nieuw = (TextView) v.findViewById(R.id.tvMenuTitle);
        nieuw.setTextColor(Color.WHITE);

        LinearLayout nieuwlayout = (LinearLayout) nieuw.getParent();
        nieuwlayout.setBackgroundColor(Color.LTGRAY);

        _selectedMenuItem = v;
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
}
