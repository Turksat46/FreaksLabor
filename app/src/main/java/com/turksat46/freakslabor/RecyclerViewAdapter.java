package com.turksat46.freakslabor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    newPerson data[];
    private Context mContext;

    private ItemClickListener clickListener;

    public RecyclerViewAdapter(Context context, newPerson[] data) {
        this.data = data;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        try {
            holder.image.setImageResource(data[position].icon);
        }catch (Exception e){
            holder.image.setImageResource(R.drawable.logo);
            Log.w("RecyclerViewAdapter", "ERROR "+e.toString());
        }

        try {
            holder.name.setText(data[position].name);
        }catch (Exception e){
            holder.name.setText("No name!");
            Log.w("RecyclerViewAdapter", "ERROR "+e.toString());
        }


        //TODO: put extras in intent
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "OnClick registered!");
                Toast.makeText(mContext, holder.name.getText(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext ,profileActivity.class);
                mContext.startActivity(intent);
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, holder.name.getText(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext ,profileActivity.class);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profileimg);
            name = itemView.findViewById(R.id.usernameText);
            card = itemView.findViewById(R.id.usercard);
        }
    }
}