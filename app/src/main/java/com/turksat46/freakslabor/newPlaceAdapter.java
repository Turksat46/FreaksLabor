package com.turksat46.freakslabor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class newPlaceAdapter extends ArrayAdapter<newPlace> {

    Context context;
    int layoutResourceId;
    newPlace data[] = null;

    public newPlaceAdapter(@NonNull Context context, int resource, newPlace[] data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        newPlaceHolder holder = null;

        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new newPlaceHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.placeimg);
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }else{
            holder = (newPlaceHolder) row.getTag();
        }

        newPlace place = data[position];
        holder.txtTitle.setText(place.name);
        holder.imgIcon.setImageResource(place.icon);
        

        return row;
    }

    static class newPlaceHolder{
        ImageView imgIcon;
        TextView txtTitle;
    }
}
