package com.turksat46.freakslabor;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class StatusBar {

    CardView statusCard;
    TextView statusText;
    ProgressBar progressBar;

    int standardColor = 0xA3373737;

    public StatusBar(CardView statusCard, TextView textView, ProgressBar progressBar){
        this.statusCard = statusCard;
        this.statusText = textView;
        this.progressBar = progressBar;
    }

    public void showWarning(String warningText){
        statusCard.setCardBackgroundColor(0xA3FF0000);
        progressBar.setVisibility(View.GONE);
        statusText.setText(warningText);
        statusCard.setVisibility(View.VISIBLE);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusCard.setVisibility(View.GONE);
                statusCard.setCardBackgroundColor(standardColor);
            }
        }, 5000);
    }

    public void showMessage(String message, int color){
        statusCard.setCardBackgroundColor(color);
        progressBar.setVisibility(View.GONE);
        statusText.setText(message);
        statusCard.setVisibility(View.VISIBLE);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusCard.setVisibility(View.GONE);
                statusCard.setCardBackgroundColor(standardColor);
            }
        }, 5000);
    }

    public void showMessage(String message){

        progressBar.setVisibility(View.GONE);
        statusText.setText(message);
        statusCard.setVisibility(View.VISIBLE);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusCard.setVisibility(View.GONE);
                statusCard.setCardBackgroundColor(standardColor);
            }
        }, 5000);
    }

    public void showSimpleLoadingProgress(String message){
        progressBar.setVisibility(View.VISIBLE);
        statusText.setText(message);
        statusCard.setVisibility(View.VISIBLE);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusCard.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                statusCard.setCardBackgroundColor(standardColor);
            }
        }, 5000);
    }

}
