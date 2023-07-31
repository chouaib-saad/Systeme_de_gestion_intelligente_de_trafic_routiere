package com.example.smartdrive.Utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;

import com.example.smartdrive.R;

public class ProgressBar {
    private final Dialog dialog;

    public ProgressBar(Context context) {
        dialog = new Dialog(context,R.style.CustomDialog);
        dialog.setContentView(R.layout.progress_bar);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}


//Run with these lines :

  //initialise progress bar
//ProgressBar customDialog = new ProgressBar(this);

// show the progressbar
//ProgressBar.show();

// dismiss the progressbar
//ProgressBar.dismiss();


