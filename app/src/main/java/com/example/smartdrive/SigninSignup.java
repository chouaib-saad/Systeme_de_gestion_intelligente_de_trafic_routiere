package com.example.smartdrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdrive.Utility.NetworkChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigninSignup extends AppCompatActivity {


    //double back button to finish
    private  boolean doubleBackToExitPressedOnce = false;

    //connection verification class
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    //for verefication userAccess level  :
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    TextView titleTv, descTv, signinTv, signupTv;

    LottieAnimationView logo;




    @Override
    public void onBackPressed() {

            if(doubleBackToExitPressedOnce){
                finishAffinity();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Appuyez a nouveau pour quitter ", Toast.LENGTH_SHORT).show();

            //time for second click
            int SECOND_CLICK_DELAY = 2000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, SECOND_CLICK_DELAY);


    }


    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

        logo.setVisibility(View.VISIBLE);


        //make animation
        Animation logoAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_left_anim);

        logoAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //logo.playAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logo.startAnimation(logoAnim);

        super.onStart();

    }


    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    //exit the app
    public void exit_btn(View view) {
        finishAffinity();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_signup);

        //initializing firebase variables :
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //logo image hook
        logo = findViewById(R.id.logo);

//        loadCustomFonts();



    } //onCreate end








    public void signin_btn(View view) {

//        ((TextView)findViewById(R.id.text_view_title)).setTextColor(getResources().getColor(R.color.baseGreen));


        //make animation
        Animation logoAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_right_anim);

        logoAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(getApplicationContext(),Signin.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                logo.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logo.startAnimation(logoAnim);


    }

    public void sigup_btn(View view) {

//        ((TextView)findViewById(R.id.text_view_title)).setTextColor(getResources().getColor(R.color.baseGreen));

        //make animation
        Animation logoAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_right_anim);

        logoAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                startActivity(new Intent(getApplicationContext(),Signup.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                logo.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logo.startAnimation(logoAnim);
    }






    //initialise fonts

    private void loadCustomFonts(){

        //textviews initialisation
        titleTv = findViewById(R.id.text_view_title);
        descTv = findViewById(R.id.text_view_desc);
        signinTv = findViewById(R.id.signinTV);
        signupTv =  findViewById(R.id.signup_TV);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Typeface typefaceAlegreya = ResourcesCompat.getFont(SigninSignup.this,R.font.alegreya_sans);
            Typeface typefaceAldrich = ResourcesCompat.getFont(SigninSignup.this,R.font.aldrich);

            titleTv.setTypeface(typefaceAlegreya);
            descTv.setTypeface(typefaceAlegreya);
            signinTv.setTypeface(typefaceAldrich);
            signupTv.setTypeface(typefaceAldrich);
        } else {
            Typeface typefaceAlegreya = ResourcesCompat.getFont(getApplicationContext(),R.font.alegreya_sans);
            Typeface typefaceAldrich = ResourcesCompat.getFont(getApplicationContext(),R.font.aldrich);

            titleTv.setTypeface(typefaceAlegreya);
            descTv.setTypeface(typefaceAlegreya);
            signinTv.setTypeface(typefaceAldrich);
            signupTv.setTypeface(typefaceAldrich);
        }
        //textview.setTypeface(typeface);



    }







}  //class end