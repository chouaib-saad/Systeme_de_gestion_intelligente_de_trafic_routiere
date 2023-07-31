package com.example.smartdrive.Utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.smartdrive.R;
import com.example.smartdrive.R;

public class NetworkChangeListener extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (!Common.isConnectedToInternet(context)) {           //internet is not connected


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog, null);
            builder.setView(layout_dialog);


            ConstraintLayout btnRetry = layout_dialog.findViewById(R.id.retry_btn);
            ProgressBar progressBar = layout_dialog.findViewById(R.id.progressbar);
            ImageView retry_icon = layout_dialog.findViewById(R.id.retry_icon);
            LottieAnimationView errorLogo = layout_dialog.findViewById(R.id.errorLogo);




            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);




            //btn retry
            btnRetry.setOnClickListener(view -> {

                retry_icon.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

            //animate layout
            btnRetry.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        retry_icon.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        dialog.dismiss();
                        onReceive(context, intent);

                    }},600);
            });


            //show dialog
            dialog.show();

        }

    }
}
