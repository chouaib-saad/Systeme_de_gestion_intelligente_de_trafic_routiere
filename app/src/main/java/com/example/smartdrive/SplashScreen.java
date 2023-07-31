package com.example.smartdrive;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdrive.Utility.ProgressBar;
import com.example.smartdrive.home.page.frags.SmartFeux;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private TextView  slogan ;
    private LinearLayout textLogo,poweredby;
    private LottieAnimationView logo ;
    private View topView1 , topView2 , topView3 ;
    private View bottomView1 , bottomView2 , bottomView3 ;


    ProgressBar progressBar;


    private boolean isRememberMeChecked;


    //for verefication userAccess level  :
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    //incrimental text counter var
    private int count = 0;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        progressBar = new ProgressBar(this);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.splash_screen);




        isRememberMeChecked = isRememberMeChecked();


        //initializing firebase variables :
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();




        textLogo = findViewById(R.id.textLogo);
        poweredby = findViewById(R.id.poweredby);
        slogan = findViewById(R.id.slogan);
        logo = findViewById(R.id.logo);

        topView1 = findViewById(R.id.topView1);
        topView2 = findViewById(R.id.topView2);
        topView3 = findViewById(R.id.topView3);

        bottomView1 = findViewById(R.id.bottomView1);
        bottomView2 = findViewById(R.id.bottomView2);
        bottomView3 = findViewById(R.id.bottomView3);



        //first logo anim
        //Animation logoAnimation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.zoom_animation);
        //new logo anim
        Animation logoAnimation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.move_left_anim);
        //animatoion with popup
        //Animation nameAnimation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.zoom_animation);
        //animation with transparancy
        Animation nameAnimation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.transparency);

        //powered by gone transparency animation and begin animation
        Animation poweredbyTransparencyAnimation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.transparency_gone);
        Animation poweredbyZoomAnimation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.zoom_animation);

        Animation topView1Animation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.top_views_animations);
        Animation topView2Animation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.top_views_animations);
        Animation topView3Animation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.top_views_animations);

        Animation bottomView1Animaton = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.bottom_views_animations);
        Animation bottomView2Animaton = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.bottom_views_animations);
        Animation bottomView3Animaton = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.bottom_views_animations);


        topView1.startAnimation(topView1Animation);
        bottomView1.startAnimation(bottomView1Animaton);
        poweredby.setAnimation(poweredbyZoomAnimation);

        topView1.setVisibility(View.VISIBLE);
        bottomView1.setVisibility(View.VISIBLE);
        poweredby.setVisibility(View.VISIBLE);

        topView1Animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                topView2.setVisibility(View.VISIBLE);
                bottomView2.setVisibility(View.VISIBLE);

                topView2.startAnimation(topView2Animation);
                bottomView2.startAnimation(bottomView2Animaton);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



        topView2Animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                topView3.setVisibility(View.VISIBLE);
                bottomView3.setVisibility(View.VISIBLE);

                topView3.startAnimation(topView3Animation);
                bottomView3.startAnimation(bottomView3Animaton);


                poweredby.setAnimation(poweredbyTransparencyAnimation);

                poweredbyTransparencyAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        poweredby.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        topView3Animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                logo.setVisibility(View.VISIBLE);
                logo.setAnimation(logoAnimation);



            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                textLogo.setVisibility(View.VISIBLE);
                textLogo.startAnimation(nameAnimation);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        nameAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                slogan.setVisibility(View.VISIBLE);


                final String animateTxt = slogan.getText().toString();
                slogan.setText("");
                count = 0;
                new CountDownTimer(animateTxt.length() * 100L, 100) {
                    StringBuilder sb = new StringBuilder();

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long l) {
                        if (count < animateTxt.length()) {
                            sb.append(animateTxt.charAt(count));
                            slogan.setText(sb.toString());
                            count++;
                        }
                    }

                    @Override
                    public void onFinish() {

                        // the countdown is finished.


                        slogan.setTextColor(getResources().getColor(R.color.baseGreen));
                        Animation logoAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_right_anim);

                        logoAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                logo.setVisibility(View.INVISIBLE);
                                progressBar.show();

                                //if no user is logged in
                                if(FirebaseAuth.getInstance().getCurrentUser() ==  null){

                                    //start SigninSignup activity
//                                    startActivity(new Intent(getApplicationContext(), SigninSignup.class));
//                                    finish();

                                    progressBar.dismiss();

                                    Intent intent = new Intent(SplashScreen.this, SigninSignup.class);
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,
//                                            Pair.create(logo, "logo_transition"),
                                            Pair.create(textLogo, "logo_text_transition"),
                                            Pair.create(slogan, "slogan_transition")
                                    );
                                    startActivity(intent, options.toBundle());


                                }else{

                                    if(isRememberMeChecked){

                                        //start the map activity services on background (a verifier)
//                                        Intent serviceIntent = new Intent(getApplicationContext(), SmartFeux.class);
//                                        startForegroundService(serviceIntent);


                                        checkUserAccessLevel(FirebaseAuth.getInstance().getCurrentUser().getUid());


                                    }else{

                                        progressBar.dismiss();

                                        Intent intent = new Intent(SplashScreen.this, SigninSignup.class);
                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,
//                                            Pair.create(logo, "logo_transition"),
                                                Pair.create(textLogo, "logo_text_transition"),
                                                Pair.create(slogan, "slogan_transition")
                                        );
                                        startActivity(intent, options.toBundle());


                                    }



                                }


                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        logo.startAnimation(logoAnim);

                    }


            }.start();



            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



    }          //   end On create view





























    private void checkUserAccessLevel(String uid) {

        DocumentReference df = fStore.collection("Users").document(uid);
        //extract the data from the user document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //this snapshot contains the partucular document data for this user (not necessary)
                Log.d("TAG", "onSuccess: " + documentSnapshot.getId());
                //identify the user access level

                if (Boolean.TRUE.equals(documentSnapshot.getBoolean("REGULAR"))) {
                    progressBar.dismiss();


                    //user is normal
                    startActivity(new Intent(getApplicationContext(), UserScreen.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    SplashScreen.super.finish();

                }else if (Boolean.TRUE.equals(documentSnapshot.getBoolean("VIP"))){
                    progressBar.dismiss();

                    //user is admin
                    startActivity(new Intent(getApplicationContext(), VipScreen.class));
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    SplashScreen.super.finish();


                }else{
                    progressBar.dismiss();

                    //if no user category selected or identified => default screen
                    Intent intent = new Intent(SplashScreen.this, SigninSignup.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,
                            Pair.create(logo, "logo_transition"),
                            Pair.create(textLogo, "logo_text_transition"),
                            Pair.create(slogan, "slogan_transition")
                    );
                    startActivity(intent, options.toBundle());

                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //refrech the page and signout the user (les donnes ne pas existe ou incomplete)
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SplashScreen.this, SigninSignup.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,
                        Pair.create(logo, "logo_transition"),
                        Pair.create(textLogo, "logo_text_transition"),
                        Pair.create(slogan, "slogan_transition")
                );
                startActivity(intent, options.toBundle());
            }
        });


    }




    private Boolean isRememberMeChecked(){
        //initialise sharedPreference state
        SharedPreferences sharedPreferences = getSharedPreferences("remember_me", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isChecked",false);
    }




}