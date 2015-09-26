package com.oktoberhackfest.remember;

import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class NearPlaceFragment extends ListFragment {
    private List<NearPlaceListItem> mItems;        // ListView items list

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the items list
        mItems = new ArrayList<NearPlaceListItem>();
        Resources resources = getResources();

        mItems.add(new NearPlaceListItem("Pimmel"));

        // initialize and set the list adapter
        setListAdapter(new NearPlaceListAdapter(getActivity(), mItems));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        NearPlaceListItem item = mItems.get(position);

        // do something
        Toast.makeText(getActivity(), item.nearPlaceName, Toast.LENGTH_SHORT).show();
    }
}
