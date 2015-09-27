package com.oktoberhackfest.remember;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    private NearPlaceListAdapter listAdapter;

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
        for (RealmPlace pl : realmPlaces) {
            mItems.add(new NearPlaceListItem(pl.getName(), pl.getAddress(), pl.getDate()));
        }
        // initialize and set the list adapter
        listAdapter = new NearPlaceListAdapter(inflater.getContext(), mItems);
        setListAdapter(listAdapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
        /*if (getListView().getEmptyView() == null) {
            View empty = getLayoutInflater(savedInstanceState).inflate(R.layout.empty_list, container);
            TextView t = (TextView) empty.findViewById(R.id.textView);
            t.setText(t.getText() + " " + new String(Character.toChars(0x1F61E)));

            getListView().setEmptyView(empty);
        }*/
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        NearPlaceListItem item = mItems.get(position);

        // do something
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(item.nearPlaceName + ", " + item.nearPlaceAddress));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public NearPlaceListAdapter getNearPlaceListAdapter() {
        return listAdapter;
    }
}
