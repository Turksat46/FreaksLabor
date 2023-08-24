package com.turksat46.freakslabor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter<PersonRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    newPerson data[];
    private Context mContext;

    private ItemClickListener clickListener;

    ViewHolder viewholder;

    //Firebase
    FirebaseFirestoreSettings fbsettings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int gradientColor = 0;

    public PersonRecyclerViewAdapter(Context context, newPerson[] data) {
        this.data = data;
        mContext = context;
        db.setFirestoreSettings(fbsettings);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        viewholder = holder;

        try {
            //holder.image.setImageResource(data[position].icon);
            FirebaseImageLoader.loadImage(mContext, data[position].uid, viewholder.image);
            //Change background
            Bitmap bitmap = ((BitmapDrawable)holder.image.getDrawable()).getBitmap();
            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    gradientColor = palette.getDominantColor(Color.BLUE);
                    FillCustomColor(holder.card);
                }
            });
        }catch (Exception e){
            holder.image.setImageResource(R.drawable.logo);
            Log.w("RecyclerViewAdapter", "ERROR "+e.toString());
        }

        try {
            //holder.name.setText(data[position].name);

            FirebaseDataGrabber.getName(data[position].uid, holder.name);
        }catch (Exception e){
            holder.name.setText("No name!");
            Log.w("RecyclerViewAdapter", "ERROR "+e.toString());
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
                onClickOnProfile(position);
            }
        });



        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, holder.name.getText(), Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(mContext ,profileActivity.class);
                //intent.putExtra("name", holder.name.getText());
                //mContext.startActivity(intent);
                onClickOnProfile(position);
            }
        });
    }

    public void FillCustomColor(CardView v){
        final CardView view = v;
        view.setCardBackgroundColor(gradientColor);
    }

    public void FillCustomGradient(CardView v) {
        final CardView view = v;
        Drawable[] layers = new Drawable[1];

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient lg = new LinearGradient(
                        0,
                        0,
                        0,
                        view.getHeight(),
                        new int[] {
                                gradientColor, // please input your color from resource for color-4
                                Color.parseColor("#2E2E2E"),
                        },
                        new float[] { 0.09f, 1.8f },
                        Shader.TileMode.CLAMP);
                return lg;
            }
        };
        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());
        p.setShaderFactory(sf);
        p.setCornerRadii(new float[] { 1, 4, 4, 1, 0, 0, 0, 0 });
        layers[0] = (Drawable) p;

        LayerDrawable composite = new LayerDrawable(layers);
        view.setBackgroundDrawable(composite);
    }



    public void onClickOnProfile(int position){
        Intent intent = new Intent(mContext, profileActivity.class);
        intent.putExtra("uid", data[position].uid);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

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