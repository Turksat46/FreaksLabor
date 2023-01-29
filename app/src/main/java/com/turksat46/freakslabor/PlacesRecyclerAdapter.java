package com.turksat46.freakslabor;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlacesRecyclerAdapter extends RecyclerView.Adapter<PlacesRecyclerAdapter.PlaceViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    //vars
    newPlace data[];
    private Context mContext;

    private ItemClickListener clickListener;

    PlacesRecyclerAdapter.PlaceViewHolder viewholder;


    public PlacesRecyclerAdapter(Context context, newPlace[] data) {
        this.data = data;
        mContext = context;
    }


    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesRecyclerAdapter.PlaceViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder = holder;

        try {
            holder.image.setImageResource(data[position].icon);
        }catch (Exception e){
            holder.image.setImageResource(R.drawable.logo);
            Log.w("PlacesRecyclerAdapter", "ERROR "+e.toString());
        }

        try {
            holder.name.setText(data[position].name);
        }catch (Exception e){
            holder.name.setText("No name!");
            Log.w("PlacesRecyclerAdapter", "ERROR "+e.toString());
        }


        //TODO: put extras in intent

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "OnClick registered!");
                //Toast.makeText(mContext, holder.name.getText(), Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(mContext ,profileActivity.class);
                //intent.putExtra("name", holder.name.getText());
                //mContext.startActivity(intent);

            }
        });



        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, holder.name.getText(), Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(mContext ,profileActivity.class);
                //intent.putExtra("name", holder.name.getText());
                //mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name;
        CardView card;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.placeimg);
            name = itemView.findViewById(R.id.placenameText);
            card = itemView.findViewById(R.id.placecard);
        }
    }
}
