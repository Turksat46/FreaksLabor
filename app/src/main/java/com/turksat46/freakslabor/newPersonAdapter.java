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

public class newPersonAdapter extends ArrayAdapter<newPerson> {

    Context context;
    int layoutResourceId;
    newPerson data[] = null;

    public newPersonAdapter(@NonNull Context context, int resource, newPerson[] data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        newPersonHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new newPersonHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (newPersonHolder)row.getTag();
        }

        newPerson person = data[position];
        holder.txtTitle.setText(person.name);
        holder.imgIcon.setImageResource(person.icon);
        holder.uid=data[position].uid;

        return row;
    }

    static class newPersonHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        String uid;
    }
}
