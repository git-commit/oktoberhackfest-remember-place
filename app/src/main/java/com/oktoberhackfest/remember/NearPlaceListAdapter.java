package com.oktoberhackfest.remember;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NearPlaceListAdapter extends ArrayAdapter<NearPlaceListItem> {

    public NearPlaceListAdapter(Context context, List<NearPlaceListItem> objects) {
        super(context, R.layout.near_place_list_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.near_place_list_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.nearPlaceName = (TextView) convertView.findViewById(R.id.nearPlaceName);
            viewHolder.nearPlaceAddress = (TextView) convertView.findViewById(R.id.nearPlaceAddress);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        NearPlaceListItem item = getItem(position);
        viewHolder.nearPlaceName.setText(item.nearPlaceName);
        viewHolder.nearPlaceAddress.setText(item.nearPlaceAddress);
        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     * @see http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        TextView nearPlaceName;
        TextView nearPlaceAddress;
    }
}
