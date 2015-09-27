package com.oktoberhackfest.remember;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oktoberhackfest.remember.realmobjects.RealmPlace;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the OnFragmentInteractionListener
 * interface.
 */
public class NearPlaceFragment extends ListFragment {
    private List<NearPlaceListItem> mItems;        // ListView items list

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // initialize the items list
        mItems = new ArrayList<>();

        Realm realm = Realm.getInstance(getContext());
        RealmResults<RealmPlace> realmPlaces = realm.allObjects(RealmPlace.class);
        for (RealmPlace pl: realmPlaces) {
            mItems.add(new NearPlaceListItem(pl.getName(), pl.getAddress()));
        }
        mItems.add(new NearPlaceListItem("Test", "Test"));

        // initialize and set the list adapter
        ArrayAdapter l = new NearPlaceListAdapter(inflater.getContext(), mItems);
        setListAdapter(l);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
        TextView empty = new TextView(this.getContext());
        empty.setText("None yet :(");
        getListView().setEmptyView(empty);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        NearPlaceListItem item = mItems.get(position);

        // do something
        Toast.makeText(getActivity(), item.nearPlaceName, Toast.LENGTH_SHORT).show();
    }
}
